/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.domain

/**
 * The derived units defined for usage in the CIM. In some cases, the derived unit is equal to an SI unit. Whenever possible, the standard derived symbol is used instead of the formula for the derived unit. For example, the unit symbol Farad is defined as “F” instead of “CPerV”. In cases where a standard symbol does not exist for a derived unit, the formula for the unit is used as the unit symbol. For example, density does not have a standard symbol and so it is represented as “kgPerm3”. Except for “kg”, which is an SI unit, the unit symbols do not contain multipliers and therefore represent the base derived unit to which a multiplier can be applied as a whole.
 * Every unit symbol is treated as an unparseable text as if it were a single-letter symbol. The meaning of each unit symbol is defined by the accompanying descriptive text and not by the text contents of the unit symbol.
 * To allow the widest possible range of serializations without requiring special character handling, several substitutions are made which deviate from the format described in IEC 80000-1. The division symbol “/” is replaced by the letters “Per”. Exponents are written in plain text after the unit as “m3” instead of being formatted as “m” with a superscript of 3 or introducing a symbol as in “m^3”. The degree symbol “°” is replaced with the letters “deg”. Any clarification of the meaning for a substitution is included in the description for the unit symbol.
 * Non-SI units are included in list of unit symbols to allow sources of data to be correctly labelled with their non-SI units (for example, a GPS sensor that is reporting numbers that represent feet instead of meters). This allows software to use the unit symbol information correctly convert and scale the raw data of those sources into SI-based units.
 * The integer values are used for harmonization with IEC 61850.
 */
enum class UnitSymbol(private val value: Int, private val cimName: String) {
    /**
     * Dimension less quantity, e.g. count, per unit, etc.
     */
    NONE(0, "none"),

    /**
     * Length in metres.
     */
    METRES(1, "m"),

    /**
     * Mass in kilograms. Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
     */
    KG(2, "kg"),

    /**
     * Time in seconds.
     */
    SECONDS(3, "s"),

    /**
     * Current in amperes.
     */
    A(4, "A"),

    /**
     * Temperature in kelvins.
     */
    K(5, "K"),

    /**
     * Amount of substance in moles.
     */
    MOL(6, "mol"),

    /**
     * Luminous intensity in candelas.
     */
    CD(7, "cd"),

    /**
     * Plane angle in degrees.
     */
    DEG(8, "deg"),

    /**
     * Plane angle in radians (m/m).
     */
    RAD(9, "rad"),

    /**
     * Solid angle in steradians (m2/m2).
     */
    SR(10, "sr"),

    /**
     * Absorbed dose in grays (J/kg).
     */
    GY(11, "Gy"),

    /**
     * Radioactivity in becquerels (1/s).
     */
    BQ(12, "Bq"),

    /**
     * Relative temperature in degrees Celsius.
     * In the SI unit system the symbol is °C. Electric charge is measured in coulomb that has the unit symbol C. To distinguish degree Celsius from coulomb the symbol used in the UML is degC. The reason for not using °C is that the special character ° is difficult to manage in software.
     */
    DEGC(13, "degC"),

    /**
     * Dose equivalent in sieverts (J/kg).
     */
    SV(14, "Sv"),

    /**
     * Electric capacitance in farads (C/V).
     */
    F(15, "F"),

    /**
     * Electric charge in coulombs (A·s).
     */
    C(16, "C"),

    /**
     * Conductance in siemens.
     */
    SIEMENS(17, "S"),

    /**
     * Electric inductance in henrys (Wb/A).
     */
    HENRYS(18, "H"),

    /**
     * Electric potential in volts (W/A).
     */
    V(19, "V"),

    /**
     * Electric resistance in ohms (V/A).
     */
    OHM(20, "ohm"),

    /**
     * Energy in joules (N·m = C·V = W·s).
     */
    J(21, "J"),

    /**
     * Force in newtons (kg·m/s²).
     */
    N(22, "N"),

    /**
     * Frequency in hertz (1/s).
     */
    HZ(23, "Hz"),

    /**
     * Illuminance in lux (lm/m²).
     */
    LX(24, "lx"),

    /**
     * Luminous flux in lumens (cd·sr).
     */
    LM(25, "lm"),

    /**
     * Magnetic flux in webers (V·s).
     */
    WB(26, "Wb"),

    /**
     * Magnetic flux density in teslas (Wb/m2).
     */
    T(27, "T"),

    /**
     * Real power in watts (J/s). Electrical power may have real and reactive components. The real portion of electrical power (I²R or VIcos(phi)), is expressed in Watts. See also apparent power and reactive power.
     */
    W(28, "W"),

    /**
     * Pressure in pascals (N/m²). Note: the absolute or relative measurement of pressure is implied with this entry. See below for more explicit forms.
     */
    PA(29, "Pa"),

    /**
     * Area in square metres (m²).
     */
    M2(30, "m2"),

    /**
     * Volume in cubic metres (m³).
     */
    M3(31, "m3"),

    /**
     * Velocity in metres per second (m/s).
     */
    MPERS(32, "mPers"),

    /**
     * Acceleration in metres per second squared (m/s²).
     */
    MPERS2(33, "mPers2"),

    /**
     * Volumetric flow rate in cubic metres per second (m³/s).
     */
    M3PERS(34, "m3Pers"),

    /**
     * Fuel efficiency in metres per cubic metres (m/m³).
     */
    MPERM3(35, "mPerm3"),

    /**
     * Moment of mass in kilogram metres (kg·m) (first moment of mass). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
     */
    KGM(36, "kgm"),

    /**
     * Density in kilogram/cubic metres (kg/m³). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
     */
    KGPERM3(37, "kgPerm3"),

    /**
     * Viscosity in square metres / second (m²/s).
     */
    M2PERS(38, "m2Pers"),

    /**
     * Thermal conductivity in watt/metres kelvin.
     */
    WPERMK(39, "WPermK"),

    /**
     * Heat capacity in joules/kelvin.
     */
    JPERK(40, "JPerK"),

    /**
     * Concentration in parts per million.
     */
    PPM(41, "ppm"),

    /**
     * Rotations per second (1/s). See also Hz (1/s).
     */
    ROTPERS(42, "rotPers"),

    /**
     * Angular velocity in radians per second (rad/s).
     */
    RADPERS(43, "radPers"),

    /**
     * Heat flux density, irradiance, watts per square metre.
     */
    WPERM2(44, "WPerm2"),

    /**
     * Insulation energy density, joules per square metre or watt second per square metre.
     */
    JPERM2(45, "JPerm2"),

    /**
     * Conductance per length (F/m).
     */
    SPERM(46, "SPerm"),

    /**
     * Temperature change rate in kelvins per second.
     */
    KPERS(47, "KPers"),

    /**
     * Pressure change rate in pascals per second.
     */
    PAPERS(48, "PaPers"),

    /**
     * Specific heat capacity, specific entropy, joules per kilogram Kelvin.
     */
    JPERKGK(49, "JPerkgK"),

    /**
     * Apparent power in volt amperes. See also real power and reactive power.
     */
    VA(50, "VA"),

    /**
     * Reactive power in volt amperes reactive. The “reactive” or “imaginary” component of electrical power (VIsin(phi)). (See also real power and apparent power).
     * Note: Different meter designs use different methods to arrive at their results. Some meters may compute reactive power as an arithmetic value, while others compute the value vectorially. The data consumer should determine the method in use and the suitability of the measurement for the intended purpose.
     */
    VAR(51, "VAr"),

    /**
     * Power factor, dimensionless.
     * Note 1: This definition of power factor only holds for balanced systems. See the alternative definition under code 153.
     * Note 2 : Beware of differing sign conventions in use between the IEC and EEI. It is assumed that the data consumer understands the type of meter in use and the sign convention in use by the utility.
     */
    COSPHI(52, "cosPhi"),

    /**
     * Volt seconds (Ws/A).
     */
    VS(53, "Vs"),

    /**
     * Volt squared (W²/A²).
     */
    V2(54, "V2"),

    /**
     * Ampere seconds (A·s).
     */
    AS(55, "As"),

    /**
     * Amperes squared (A²).
     */
    A2(56, "A2"),

    /**
     * Ampere squared time in square amperes (A²s).
     */
    A2S(57, "A2s"),

    /**
     * Apparent energy in volt ampere hours.
     */
    VAH(58, "VAh"),

    /**
     * Real energy in watt hours.
     */
    WH(59, "Wh"),

    /**
     * Reactive energy in volt ampere reactive hours.
     */
    VARH(60, "VArh"),

    /**
     * Magnetic flux in volt per hertz.
     */
    VPERHZ(61, "VPerHz"),

    /**
     * Rate of change of frequency in hertz per second.
     */
    HZPERS(62, "HzPers"),

    /**
     * Number of characters.
     */
    CHARACTER(63, "character"),

    /**
     * Data rate (baud) in characters per second.
     */
    CHARPERS(64, "charPers"),

    /**
     * Moment of mass in kilogram square metres (kg·m²) (Second moment of mass, commonly called the moment of inertia). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
     */
    KGM2(65, "kgm2"),

    /**
     * Sound pressure level in decibels. Note: multiplier “d” is included in this unit symbol for compatibility with IEC 61850-7-3.
     */
    DB(66, "dB"),

    /**
     * Ramp rate in watts per second.
     */
    WPERS(67, "WPers"),

    /**
     * Volumetric flow rate in litres per second.
     */
    LPERS(68, "lPers"),

    /**
     * Power level (logarithmic ratio of signal strength , Bel-mW), normalized to 1mW. Note: multiplier “d” is included in this unit symbol for compatibility with IEC 61850-7-3.
     */
    DBM(69, "dBm"),

    /**
     * Time in hours, hour = 60 min = 3600 s.
     */
    HOURS(70, "h"),

    /**
     * Time in minutes, minute = 60 s.
     */
    MIN(71, "min"),

    /**
     * Quantity power, Q.
     */
    Q(72, "Q"),

    /**
     * Quantity energy, Qh.
     */
    QH(73, "Qh"),

    /**
     * Resistivity, ohm metres, (rho).
     */
    OHMM(74, "ohmm"),

    /**
     * A/m, magnetic field strength, amperes per metre.
     */
    APERM(75, "APerm"),

    /**
     * Volt-squared hour, volt-squared-hours.
     */
    V2H(76, "V2h"),

    /**
     * Ampere-squared hour, ampere-squared hour.
     */
    A2H(77, "A2h"),

    /**
     * Ampere-hours, ampere-hours.
     */
    AH(78, "Ah"),

    /**
     * Amount of substance, Counter value.
     */
    COUNT(79, "count"),

    /**
     * Volume, cubic feet.
     */
    FT3(80, "ft3"),

    /**
     * Volumetric flow rate, cubic metres per hour.
     */
    M3PERH(81, "m3Perh"),

    /**
     * Volume in gallons, US gallon (1 gal = 231 in3 = 128 fl ounce).
     */
    GAL(82, "gal"),

    /**
     * Energy, British Thermal Units.
     */
    BTU(83, "Btu"),

    /**
     * Volume in litres, litre = dm3 = m3/1000.
     */
    L(84, "l"),

    /**
     * Volumetric flow rate, litres per hour.
     */
    LPERH(85, "lPerh"),

    /**
     * Concentration, The ratio of the volume of a solute divided by the volume of the solution. Note: Users may need use a prefix such a ‘µ’ to express a quantity such as ‘µL/L’.
     */
    LPERL(86, "lPerl"),

    /**
     * Concentration, The ratio of the mass of a solute divided by the mass of the solution. Note: Users may need use a prefix such a ‘µ’ to express a quantity such as ‘µg/g’.
     */
    GPERG(87, "gPerg"),

    /**
     * Concentration, The amount of substance concentration, (c), the amount of solvent in moles divided by the volume of solution in m³.
     */
    MOLPERM3(88, "molPerm3"),

    /**
     * Concentration, Molar fraction, the ratio of the molar amount of a solute divided by the molar amount of the solution.
     */
    MOLPERMOL(89, "molPermol"),

    /**
     * Concentration, Molality, the amount of solute in moles and the amount of solvent in kilograms.
     */
    MOLPERKG(90, "molPerkg"),

    /**
     * Time, Ratio of time. Note: Users may need to supply a prefix such as ‘µ’ to show rates such as ‘µs/s’.
     */
    SPERS(91, "sPers"),

    /**
     * Frequency, rate of frequency change. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mHz/Hz’.
     */
    HZPERHZ(92, "HzPerHz"),

    /**
     * Voltage, ratio of voltages. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mV/V’.
     */
    VPERV(93, "VPerV"),

    /**
     * Current, ratio of amperages. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mA/A’.
     */
    APERA(94, "APerA"),

    /**
     * Power factor, PF, the ratio of the active power to the apparent power. Note: The sign convention used for power factor will differ between IEC meters and EEI (ANSI) meters. It is assumed that the data consumers understand the type of meter being used and agree on the sign convention in use at any given utility.
     */
    VPERVA(95, "VPerVA"),

    /**
     * Amount of rotation, revolutions.
     */
    REV(96, "rev"),

    /**
     * Catalytic activity, katal = mol / s.
     */
    KAT(97, "kat"),

    /**
     * Specific energy, Joules / kg.
     */
    JPERKG(98, "JPerkg"),

    /**
     * Volume, cubic metres, with the value uncompensated for weather effects.
     */
    M3UNCOMPENSATED(99, "m3Uncompensated"),

    /**
     * Volume, cubic metres, with the value compensated for weather effects.
     */
    M3COMPENSATED(100, "m3Compensated"),

    /**
     * Signal Strength, ratio of power. Note: Users may need to supply a prefix such as ‘m’ to show rates such as ‘mW/W’.
     */
    WPERW(101, "WPerW"),

    /**
     * Energy, therms.
     */
    THERM(102, "therm"),

    /**
     * Wavenumber, reciprocal metres, (1/m).
     */
    ONEPERM(103, "onePerm"),

    /**
     * Specific volume, cubic metres per kilogram, v.
     */
    M3PERKG(104, "m3Perkg"),

    /**
     * Dynamic viscosity, pascal seconds.
     */
    PAS(105, "Pas"),

    /**
     * Moment of force, newton metres.
     */
    NM(106, "Nm"),

    /**
     * Surface tension, newton per metre.
     */
    NPERM(107, "NPerm"),

    /**
     * Angular acceleration, radians per second squared.
     */
    RADPERS2(108, "radPers2"),

    /**
     * Energy density, joules per cubic metre.
     */
    JPERM3(109, "JPerm3"),

    /**
     * Electric field strength, volts per metre.
     */
    VPERM(110, "VPerm"),

    /**
     * Electric charge density, coulombs per cubic metre.
     */
    CPERM3(111, "CPerm3"),

    /**
     * Surface charge density, coulombs per square metre.
     */
    CPERM2(112, "CPerm2"),

    /**
     * Permittivity, farads per metre.
     */
    FPERM(113, "FPerm"),

    /**
     * Permeability, henrys per metre.
     */
    HPERM(114, "HPerm"),

    /**
     * Molar energy, joules per mole.
     */
    JPERMOL(115, "JPermol"),

    /**
     * Molar entropy, molar heat capacity, joules per mole kelvin.
     */
    JPERMOLK(116, "JPermolK"),

    /**
     * Exposure (x rays), coulombs per kilogram.
     */
    CPERKG(117, "CPerkg"),

    /**
     * Absorbed dose rate, grays per second.
     */
    GYPERS(118, "GyPers"),

    /**
     * Radiant intensity, watts per steradian.
     */
    WPERSR(119, "WPersr"),

    /**
     * Radiance, watts per square metre steradian.
     */
    WPERM2SR(120, "WPerm2sr"),

    /**
     * Catalytic activity concentration, katals per cubic metre.
     */
    KATPERM3(121, "katPerm3"),

    /**
     * Time in days, day = 24 h = 86400 s.
     */
    D(122, "d"),

    /**
     * Plane angle, minutes.
     */
    ANGLEMIN(123, "anglemin"),

    /**
     * Plane angle, seconds.
     */
    ANGLESEC(124, "anglesec"),

    /**
     * Area, hectares.
     */
    HA(125, "ha"),

    /**
     * Mass in tons, “tonne” or “metric ton” (1000 kg = 1 Mg).
     */
    TONNE(126, "tonne"),

    /**
     * Pressure in bars, (1 bar = 100 kPa).
     */
    BAR(127, "bar"),

    /**
     * Pressure, millimetres of mercury (1 mmHg is approximately 133.3 Pa).
     */
    MMHG(128, "mmHg"),

    /**
     * Length, nautical miles (1 M = 1852 m).
     */
    MILES_NAUTICAL(129, "M"),

    /**
     * Speed, knots (1 kn = 1852/3600) m/s.
     */
    KN(130, "kn"),

    /**
     * Magnetic flux, maxwells (1 Mx = 10-8 Wb).
     */
    MX(131, "Mx"),

    /**
     * Magnetic flux density, gausses (1 G = 10-4 T).
     */
    G(132, "G"),

    /**
     * Magnetic field in oersteds, (1 Oe = (103/4p) A/m).
     */
    OE(133, "Oe"),

    /**
     * Volt-hour, Volt hours.
     */
    VH(134, "Vh"),

    /**
     * Active power per current flow, watts per Ampere.
     */
    WPERA(135, "WPerA"),

    /**
     * Reciprocal of frequency (1/Hz).
     */
    ONEPERHZ(136, "onePerHz"),

    /**
     * Power factor, PF, the ratio of the active power to the apparent power. Note: The sign convention used for power factor will differ between IEC meters and EEI (ANSI) meters. It is assumed that the data consumers understand the type of meter being used and agree on the sign convention in use at any given utility.
     */
    VPERVAR(137, "VPerVAr"),

    /**
     * Electric resistance per length in ohms per metre ((V/A)/m).
     */
    OHMPERM(138, "ohmPerm"),

    /**
     * Weight per energy in kilograms per joule (kg/J). Note: multiplier “k” is included in this unit symbol for compatibility with IEC 61850-7-3.
     */
    KGPERJ(139, "kgPerJ"),

    /**
     * Energy rate in joules per second (J/s).
     */
    JPERS(140, "JPers");

    fun value(): Int {
        return value
    }

    override fun toString(): String {
        return cimName
    }

    companion object {
        private val map = entries.associateBy(UnitSymbol::cimName)

        @JvmStatic
        fun fromCimName(value: String): UnitSymbol? = map[value]
    }
}
