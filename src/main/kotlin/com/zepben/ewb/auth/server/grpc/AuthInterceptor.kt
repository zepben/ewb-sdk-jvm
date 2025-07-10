/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.server.grpc

import com.auth0.jwt.interfaces.DecodedJWT
import com.zepben.ewb.auth.common.StatusCode
import com.zepben.ewb.auth.server.AuthResponse
import com.zepben.ewb.auth.server.JWTAuthoriser
import com.zepben.ewb.auth.server.TokenAuthenticator
import io.grpc.*
import io.grpc.Metadata.ASCII_STRING_MARSHALLER


val AUTHORIZATION_METADATA_KEY: Metadata.Key<String> = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER)
const val BEARER_TYPE = "Bearer"

fun statusCodeToStatus(statusCode: StatusCode): Status =
    when (statusCode) {
        StatusCode.OK -> Status.OK
        StatusCode.PERMISSION_DENIED -> Status.PERMISSION_DENIED
        StatusCode.UNAUTHENTICATED -> Status.UNAUTHENTICATED
        StatusCode.UNKNOWN -> Status.UNKNOWN
        else -> Status.UNKNOWN
    }

fun authRespToGrpcAuthResp(response: AuthResponse): GrpcAuthResp =
    GrpcAuthResp(
        statusCodeToStatus(response.statusCode).withDescription(response.message).withCause(response.cause)
    )

data class GrpcAuthResp(val status: Status, val token: DecodedJWT? = null)

/**
 * Intercepts, authenticates, and authorises gRPC calls.
 *
 * @property tokenAuthenticator The [TokenAuthenticator] to use for authenticating tokens.
 * @param requiredScopes A map of gRPC descriptors to their corresponding required scopes. If an empty set of scopes is provided, no authorisation
 * is necessary for the provided descriptor.
 * @property authorise Callback to authorise a taken. Will be provided with the gRPC service name as per [serverCall.methodDescriptor.serviceName] and the JWT.
 * Must return a [GrpcAuthResp] with a valid status. By default will use [requiredScopes] and "permissions" (Auth0) or "roles" (EntraID) to determine authorisation.
 * If using the default implementation [requiredScopes] must not be null, and it must contain a valid claim for every possible gRPC serviceName.
 */
class AuthInterceptor(
    private val tokenAuthenticator: TokenAuthenticator,
    requiredScopes: Map<String, String>?,
    private val authorise: (String, DecodedJWT) -> GrpcAuthResp = { serviceName, token ->
        requiredScopes!![serviceName]?.let { claim ->
            authRespToGrpcAuthResp(JWTAuthoriser.authorise(token, claim))
        }
            ?: GrpcAuthResp(Status.UNAUTHENTICATED.withDescription("Server has not defined a permission scope for ${serviceName}. This is a bug, contact the developers."))
    }
) : ServerInterceptor {

    override fun <ReqT, RespT> interceptCall(
        serverCall: ServerCall<ReqT, RespT>,
        metadata: Metadata,
        serverCallHandler: ServerCallHandler<ReqT, RespT>?
    ): ServerCall.Listener<ReqT> {
        val value = metadata[AUTHORIZATION_METADATA_KEY]
        val authResp = if (value == null) {
            GrpcAuthResp(Status.UNAUTHENTICATED.withDescription("Authorization token is missing"))
        } else if (!value.startsWith(BEARER_TYPE)) {
            GrpcAuthResp(Status.UNAUTHENTICATED.withDescription("Unknown authorization type"))
        } else {
            try {
                val r = tokenAuthenticator.authenticate(value.substring(BEARER_TYPE.length).trim { it <= ' ' })
                if (r.statusCode === StatusCode.OK)
                    authorise(serverCall.methodDescriptor.serviceName!!, r.token!!)
                else
                    GrpcAuthResp(statusCodeToStatus(r.statusCode).withDescription(r.message).withCause(r.cause))
            } catch (ex: Exception) {
                GrpcAuthResp(statusCodeToStatus(StatusCode.UNKNOWN).withDescription(ex.message).withCause(ex))
            }
        }

        if (authResp.status === Status.OK) {
            val ctx: Context = Context.current()
            return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler)
        }
        // XXXX: Maybe in the future we should have a callback to a logger here to log failed requests? Currently the client is the only one
        //       that will see any error messages upon connection, and thus we rely on clients for reporting issues.
        serverCall.close(authResp.status, Metadata())
        return object : ServerCall.Listener<ReqT>() {} // no-op
    }
}
