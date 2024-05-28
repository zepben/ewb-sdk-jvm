---
id: sdk-overview
title: Overview
---

# EWB SDK

The EWB SDK contains everything necessary to write software for the EWB platform. Common use cases are:
- Writing ingestors to process source data into the EWB CIM data model
- Pulling network data from a EWB server for analysis

Not all features in the JVM version of this SDK are present in the Python version. See the following feature matrix:

| Feature name                     | Description                                                                                           | JVM      | Python   |
|----------------------------------|-------------------------------------------------------------------------------------------------------|----------|----------|
| CIM data model                   | The CIM data model for networks, customers, and diagrams used in EWB.                                 | &#10004; | &#10004; |
| Tracing                          | A set of utility functions that can trace through a network, accounting for connectivity/phasing.     | &#10004; | &#10004; |
| Network consumer                 | The ability to pull network data from EWB into an in-memory CIM data model.                           | &#10004; | &#10004; |
| Customer consumer                | The ability to pull customer data from EWB into an in-memory CIM data model.                          | &#10004; | &#10004; |
| Diagram consumer                 | The ability to pull diagram data from EWB into an in-memory CIM data model.                           | &#10004; | &#10004; |
| CIM database reading/writing     | The ability to save and load an in-memory CIM data model into databases.                              | &#10004; | &#10004; |
| CIM database upgrades            | The ability to upgrade a CIM database into a newer version of EWB CIM.                                | &#10004; | &#10008; |
| Metrics data model               | The data model used for representing ingestion jobs and their corresponding metrics and data sources. | &#10004; | &#10008; |
| Metrics database reading/writing | The ability to save and load an in-memory metrics data model into databases.                          | &#10004; | &#10008; |
