/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.domain

/**
 * The derived units defined for usage in the CIM. In some cases, the derived unit is equal to an SI unit. Whenever possible, the standard derived symbol is used instead of the formula for the derived unit. For example, the unit symbol Farad is defined as “F” instead of “CPerV”. In cases where a standard symbol does not exist for a derived unit, the formula for the unit is used as the unit symbol. For example, density does not have a standard symbol and so it is represented as “kgPerm3”. With the exception of the “kg”, which is an SI unit, the unit symbols do not contain multipliers and therefore represent the base derived unit to which a multiplier can be applied as a whole.
 * Every unit symbol is treated as an unparseable text as if it were a single-letter symbol. The meaning of each unit symbol is defined by the accompanying descriptive text and not by the text contents of the unit symbol.
 * To allow the widest possible range of serializations without requiring special character handling, several substitutions are made which deviate from the format described in IEC 80000-1. The division symbol “/” is replaced by the letters “Per”. Exponents are written in plain text after the unit as “m3” instead of being formatted as “m” with a superscript of 3 or introducing a symbol as in “m^3”. The degree symbol “°” is replaced with the letters “deg”. Any clarification of the meaning for a substitution is included in the description for the unit symbol.
 * Non-SI units are included in list of unit symbols to allow sources of data to be correctly labelled with their non-SI units (for example, a GPS sensor that is reporting numbers that represent feet instead of meters). This allows software to use the unit symbol information correctly convert and scale the raw data of those sources into SI-based units.
 * The integer values are used for harmonization with IEC 61850.
 *
 * @property NONE Dimension less quantity, e.g. count, per unit, etc.
 * @property METRES Length in metres.
 * @property KG Mass in kilograms. Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
 * @property SECONDS Time in seconds.
 * @property A Current in amperes.
 * @property K Temperature in kelvins.
 * @property MOL Amount of substance in moles.
 * @property CD Luminous intensity in candelas.
 * @property DEG Plane angle in degrees.
 * @property RAD Plane angle in radians (m/m).
 * @property SR Solid angle in steradians (m2/m2).
 * @property GY Absorbed dose in grays (J/kg).
 * @property BQ Radioactivity in becquerels (1/s).
 * @property DEGC Relative temperature in degrees Celsius.
 *				  In the SI unit system the symbol is °C. Electric charge is measured in coulomb that has the unit symbol C. To distinguish degree Celsius from coulomb the symbol used in the UML is degC. The reason for not using °C is that the special character ° is difficult to manage in software."
 * @property SV Dose equivalent in sieverts (J/kg).
 * @property F Electric capacitance in farads (C/V).
 * @property C Electric charge in coulombs (A·s).
 * @property SIEMENS Conductance in siemens.
 * @property HENRYS Electric inductance in henrys (Wb/A).
 * @property V Electric potential in volts (W/A).
 * @property OHM Electric resistance in ohms (V/A).
 * @property J Energy in joules (N·m = C·V = W·s).
 * @property N Force in newtons (kg·m/s²).
 * @property HZ Frequency in hertz (1/s).
 * @property LX Illuminance in lux (lm/m²).
 * @property LM Luminous flux in lumens (cd·sr).
 * @property WB Magnetic flux in webers (V·s).
 * @property T Magnetic flux density in teslas (Wb/m2).
 * @property W Real power in watts (J/s). Electrical power may have real and reactive components. The real portion of electrical power (I²R or VIcos(phi)), is expressed in Watts. See also apparent power and reactive power.
 * @property PA Pressure in pascals (N/m²). Note: the absolute or relative measurement of pressure is implied with this entry. See below for more explicit forms.
 * @property M2 Area in square metres (m²).
 * @property M3 Volume in cubic metres (m³).
 * @property MPERS Velocity in metres per second (m/s).
 * @property MPERS2 Acceleration in metres per second squared (m/s²).
 * @property M3PERS Volumetric flow rate in cubic metres per second (m³/s).
 * @property MPERM3 Fuel efficiency in metres per cubic metres (m/m³).
 * @property KGM Moment of mass in kilogram metres (kg·m) (first moment of mass). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
 * @property KGPERM3 Density in kilogram/cubic metres (kg/m³). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
 * @property M2PERS Viscosity in square metres / second (m²/s).
 * @property WPERMK Thermal conductivity in watt/metres kelvin.
 * @property JPERK Heat capacity in joules/kelvin.
 * @property PPM Concentration in parts per million.
 * @property ROTPERS Rotations per second (1/s). See also Hz (1/s).
 * @property RADPERS Angular velocity in radians per second (rad/s).
 * @property WPERM2 Heat flux density, irradiance, watts per square metre.
 * @property JPERM2 Insulation energy density, joules per square metre or watt second per square metre.
 * @property SPERM Conductance per length (F/m).
 * @property KPERS Temperature change rate in kelvins per second.
 * @property PAPERS Pressure change rate in pascals per second.
 * @property JPERKGK Specific heat capacity, specific entropy, joules per kilogram Kelvin.
 * @property VA Apparent power in volt amperes. See also real power and reactive power.
 * @property VAR Reactive power in volt amperes reactive. The “reactive” or “imaginary” component of electrical power (VIsin(phi)). (See also real power and apparent power).
 * 				 Note: Different meter designs use different methods to arrive at their results. Some meters may compute reactive power as an arithmetic value, while others compute the value vectorially. The data consumer should determine the method in use and the suitability of the measurement for the intended purpose."
 * @property COSPHI Power factor, dimensionless.
 * 				    Note 1: This definition of power factor only holds for balanced systems. See the alternative definition under code 153.
 * 				    Note 2 : Beware of differing sign conventions in use between the IEC and EEI. It is assumed that the data consumer understands the type of meter in use and the sign convention in use by the utility."
 * @property VS Volt seconds (Ws/A).
 * @property V2 Volt squared (W²/A²).
 * @property AS Ampere seconds (A·s).
 * @property A2 Amperes squared (A²).
 * @property A2S Ampere squared time in square amperes (A²s).
 * @property VAH Apparent energy in volt ampere hours.
 * @property WH Real energy in watt hours.
 * @property VARH Reactive energy in volt ampere reactive hours.
 * @property VPERHZ Magnetic flux in volt per hertz.
 * @property HZPERS Rate of change of frequency in hertz per second.
 * @property CHARACTER Number of characters.
 * @property CHARPERS Data rate (baud) in characters per second.
 * @property KGM2 Moment of mass in kilogram square metres (kg·m²) (Second moment of mass, commonly called the moment of inertia). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
 * @property DB Sound pressure level in decibels. Note: multiplier “d” is included in this unit symbol for compatibility with IEC 61850-7-3.
 * @property WPERS Ramp rate in watts per second.
 * @property LPERS Volumetric flow rate in litres per second.
 * @property DBM Power level (logarithmic ratio of signal strength , Bel-mW), normalized to 1mW. Note: multiplier “d” is included in this unit symbol for compatibility with IEC 61850-7-3.
 * @property HOURS Time in hours, hour = 60 min = 3600 s.
 * @property MIN Time in minutes, minute = 60 s.
 * @property Q Quantity power, Q.
 * @property QH Quantity energy, Qh.
 * @property OHMM Resistivity, ohm metres, (rho).
 * @property APERM A/m, magnetic field strength, amperes per metre.
 * @property V2H Volt-squared hour, volt-squared-hours.
 * @property A2H Ampere-squared hour, ampere-squared hour.
 * @property AH Ampere-hours, ampere-hours.
 * @property COUNT Amount of substance, Counter value.
 * @property FT3 Volume, cubic feet.
 * @property M3PERH Volumetric flow rate, cubic metres per hour.
 * @property GAL Volume in gallons, US gallon (1 gal = 231 in3 = 128 fl ounce).
 * @property BTU Energy, British Thermal Units.
 * @property L Volume in litres, litre = dm3 = m3/1000.
 * @property LPERH Volumetric flow rate, litres per hour.
 * @property LPERL Concentration, The ratio of the volume of a solute divided by the volume of the solution. Note: Users may need use a prefix such a ‘µ’ to express a quantity such as ‘µL/L’.
 * @property GPERG Concentration, The ratio of the mass of a solute divided by the mass of the solution. Note: Users may need use a prefix such a ‘µ’ to express a quantity such as ‘µg/g’.
 * @property MOLPERM3 Concentration, The amount of substance concentration, (c), the amount of solvent in moles divided by the volume of solution in m³.
 * @property MOLPERMOL Concentration, Molar fraction, the ratio of the molar amount of a solute divided by the molar amount of the solution.
 * @property MOLPERKG Concentration, Molality, the amount of solute in moles and the amount of solvent in kilograms.
 * @property SPERS Time, Ratio of time. Note: Users may need to supply a prefix such as ‘µ’ to show rates such as ‘µs/s’.
 * @property HZPERHZ Frequency, rate of frequency change. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mHz/Hz’.
 * @property VPERV Voltage, ratio of voltages. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mV/V’.
 * @property APERA Current, ratio of amperages. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mA/A’.
 * @property VPERVA Power factor, PF, the ratio of the active power to the apparent power. Note: The sign convention used for power factor will differ between IEC meters and EEI (ANSI) meters. It is assumed that the data consumers understand the type of meter being used and agree on the sign convention in use at any given utility.
 * @property REV Amount of rotation, revolutions.
 * @property KAT Catalytic activity, katal = mol / s.
 * @property JPERKG Specific energy, Joules / kg.
 * @property M3UNCOMPENSATED Volume, cubic metres, with the value uncompensated for weather effects.
 * @property M3COMPENSATED Volume, cubic metres, with the value compensated for weather effects.
 * @property WPERW Signal Strength, ratio of power. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mW/W’.
 * @property THERM Energy, therms.
 * @property ONEPERM Wavenumber, reciprocal metres, (1/m).
 * @property M3PERKG Specific volume, cubic metres per kilogram, v.
 * @property PAS Dynamic viscosity, pascal seconds.
 * @property NM Moment of force, newton metres.
 * @property NPERM Surface tension, newton per metre.
 * @property RADPERS2 Angular acceleration, radians per second squared.
 * @property JPERM3 Energy density, joules per cubic metre.
 * @property VPERM Electric field strength, volts per metre.
 * @property CPERM3 Electric charge density, coulombs per cubic metre.
 * @property CPERM2 Surface charge density, coulombs per square metre.
 * @property FPERM Permittivity, farads per metre.
 * @property HPERM Permeability, henrys per metre.
 * @property JPERMOL Molar energy, joules per mole.
 * @property JPERMOLK Molar entropy, molar heat capacity, joules per mole kelvin.
 * @property CPERKG Exposure (x rays), coulombs per kilogram.
 * @property GYPERS Absorbed dose rate, grays per second.
 * @property WPERSR Radiant intensity, watts per steradian.
 * @property WPERM2SR Radiance, watts per square metre steradian.
 * @property KATPERM3 Catalytic activity concentration, katals per cubic metre.
 * @property D Time in days, day = 24 h = 86400 s.
 * @property ANGLEMIN Plane angle, minutes.
 * @property ANGLESEC Plane angle, seconds.
 * @property HA Area, hectares.
 * @property TONNE Mass in tons, “tonne” or “metric ton” (1000 kg = 1 Mg).
 * @property BAR Pressure in bars, (1 bar = 100 kPa).
 * @property MMHG Pressure, millimetres of mercury (1 mmHg is approximately 133.3 Pa).
 * @property MILES_NAUTICAL Length, nautical miles (1 M = 1852 m).
 * @property KN Speed, knots (1 kn = 1852/3600) m/s.
 * @property MX Magnetic flux, maxwells (1 Mx = 10-8 Wb).
 * @property G Magnetic flux density, gausses (1 G = 10-4 T).
 * @property OE Magnetic field in oersteds, (1 Oe = (103/4p) A/m).
 * @property VH Volt-hour, Volt hours.
 * @property WPERA Active power per current flow, watts per Ampere.
 * @property ONEPERHZ Reciprocal of frequency (1/Hz).
 * @property VPERVAR Power factor, PF, the ratio of the active power to the apparent power. Note: The sign convention used for power factor will differ between IEC meters and EEI (ANSI) meters. It is assumed that the data consumers understand the type of meter being used and agree on the sign convention in use at any given utility.
 * @property OHMPERM Electric resistance per length in ohms per metre ((V/A)/m).
 * @property KGPERJ Weight per energy in kilograms per joule (kg/J). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
 * @property JPERS Energy rate in joules per second (J/s).
 */
enum class UnitSymbol(private val value: Int, private val cimName: String) {
    NONE(0, "none"),
    METRES(1, "m"),
    KG(2, "kg"),
    SECONDS(3, "s"),
    A(4, "A"),
    K(5, "K"),
    MOL(6, "mol"),
    CD(7, "cd"),
    DEG(8, "deg"),
    RAD(9, "rad"),
    SR(10, "sr"),
    GY(11, "Gy"),
    BQ(12, "Bq"),
    DEGC(13, "degC"),
    SV(14, "Sv"),
    F(15, "F"),
    C(16, "C"),
    SIEMENS(17, "S"),
    HENRYS(18, "H"),
    V(19, "V"),
    OHM(20, "ohm"),
    J(21, "J"),
    N(22, "N"),
    HZ(23, "Hz"),
    LX(24, "lx"),
    LM(25, "lm"),
    WB(26, "Wb"),
    T(27, "T"),
    W(28, "W"),
    PA(29, "Pa"),
    M2(30, "m2"),
    M3(31, "m3"),
    MPERS(32, "mPers"),
    MPERS2(33, "mPers2"),
    M3PERS(34, "m3Pers"),
    MPERM3(35, "mPerm3"),
    KGM(36, "kgm"),
    KGPERM3(37, "kgPerm3"),
    M2PERS(38, "m2Pers"),
    WPERMK(39, "WPermK"),
    JPERK(40, "JPerK"),
    PPM(41, "ppm"),
    ROTPERS(42, "rotPers"),
    RADPERS(43, "radPers"),
    WPERM2(44, "WPerm2"),
    JPERM2(45, "JPerm2"),
    SPERM(46, "SPerm"),
    KPERS(47, "KPers"),
    PAPERS(48, "PaPers"),
    JPERKGK(49, "JPerkgK"),
    VA(50, "VA"),
    VAR(51, "VAr"),
    COSPHI(52, "cosPhi"),
    VS(53, "Vs"),
    V2(54, "V2"),
    AS(55, "As"),
    A2(56, "A2"),
    A2S(57, "A2s"),
    VAH(58, "VAh"),
    WH(59, "Wh"),
    VARH(60, "VArh"),
    VPERHZ(61, "VPerHz"),
    HZPERS(62, "HzPers"),
    CHARACTER(63, "character"),
    CHARPERS(64, "charPers"),
    KGM2(65, "kgm2"),
    DB(66, "dB"),
    WPERS(67, "WPers"),
    LPERS(68, "lPers"),
    DBM(69, "dBm"),
    HOURS(70, "h"),
    MIN(71, "min"),
    Q(72, "Q"),
    QH(73, "Qh"),
    OHMM(74, "ohmm"),
    APERM(75, "APerm"),
    V2H(76, "V2h"),
    A2H(77, "A2h"),
    AH(78, "Ah"),
    COUNT(79, "count"),
    FT3(80, "ft3"),
    M3PERH(81, "m3Perh"),
    GAL(82, "gal"),
    BTU(83, "Btu"),
    L(84, "l"),
    LPERH(85, "lPerh"),
    LPERL(86, "lPerl"),
    GPERG(87, "gPerg"),
    MOLPERM3(88, "molPerm3"),
    MOLPERMOL(89, "molPermol"),
    MOLPERKG(90, "molPerkg"),
    SPERS(91, "sPers"),
    HZPERHZ(92, "HzPerHz"),
    VPERV(93, "VPerV"),
    APERA(94, "APerA"),
    VPERVA(95, "VPerVA"),
    REV(96, "rev"),
    KAT(97, "kat"),
    JPERKG(98, "JPerkg"),
    M3UNCOMPENSATED(99, "m3Uncompensated"),
    M3COMPENSATED(100, "m3Compensated"),
    WPERW(101, "WPerW"),
    THERM(102, "therm"),
    ONEPERM(103, "onePerm"),
    M3PERKG(104, "m3Perkg"),
    PAS(105, "Pas"),
    NM(106, "Nm"),
    NPERM(107, "NPerm"),
    RADPERS2(108, "radPers2"),
    JPERM3(109, "JPerm3"),
    VPERM(110, "VPerm"),
    CPERM3(111, "CPerm3"),
    CPERM2(112, "CPerm2"),
    FPERM(113, "FPerm"),
    HPERM(114, "HPerm"),
    JPERMOL(115, "JPermol"),
    JPERMOLK(116, "JPermolK"),
    CPERKG(117, "CPerkg"),
    GYPERS(118, "GyPers"),
    WPERSR(119, "WPersr"),
    WPERM2SR(120, "WPerm2sr"),
    KATPERM3(121, "katPerm3"),
    D(122, "d"),
    ANGLEMIN(123, "anglemin"),
    ANGLESEC(124, "anglesec"),
    HA(125, "ha"),
    TONNE(126, "tonne"),
    BAR(127, "bar"),
    MMHG(128, "mmHg"),
    MILES_NAUTICAL(129, "M"),
    KN(130, "kn"),
    MX(131, "Mx"),
    G(132, "G"),
    OE(133, "Oe"),
    VH(134, "Vh"),
    WPERA(135, "WPerA"),
    ONEPERHZ(136, "onePerHz"),
    VPERVAR(137, "VPerVAr"),
    OHMPERM(138, "ohmPerm"),
    KGPERJ(139, "kgPerJ"),
    JPERS(140, "JPers");

    fun value(): Int {
        return value
    }

    override fun toString(): String {
        return cimName
    }

    companion object {
        private val map = values().associateBy(UnitSymbol::cimName)

        @JvmStatic
        fun fromCimName(value: String): UnitSymbol? = map[value]
    }
}
