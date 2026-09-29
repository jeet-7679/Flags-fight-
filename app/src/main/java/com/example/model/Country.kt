package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Geometric flag layout pattern for drawing flags on circular balls
 */
enum class FlagPattern {
    HORIZ_TRICOLOR,     // e.g. Germany, Netherlands, Russia, Argentina
    VERT_TRICOLOR,      // e.g. France, Italy, Mexico, Nigeria, Ireland
    HORIZ_BICOLOR,       // e.g. Poland, Ukraine, Indonesia
    NORDIC_CROSS,       // e.g. Sweden, Norway, Denmark, Finland, Iceland
    CENTER_CIRCLE,      // e.g. Japan, Bangladesh, Palau
    CROSS_AND_SALTIRE,  // e.g. UK (Union Jack)
    STARS_AND_STRIPES,  // e.g. USA, Liberia, Malaysia
    BRAZIL_RHOMBUS,     // Brazil
    CANADIAN_PALE,      // Canada
    KOREA_TAEGEUK,      // South Korea
    DIAGONAL_SALTIRE,   // Jamaica, Scotland, Burundi
    SOLID_EMBLEM,       // China, Vietnam, Turkey, Switzerland, Albania, etc.
    SPAIN_CREST,        // Spain
    INDIA_CHAKRA,       // India
    SOUTH_AFRICA_Y,     // South Africa
    GREECE_STRIPES      // Greece, Uruguay
}

data class Country(
    val code: String,
    val name: String,
    val emoji: String,
    val continent: String,
    val pattern: FlagPattern,
    val color1: Color,
    val color2: Color,
    val color3: Color = Color.Transparent,
    val color4: Color = Color.Transparent,
    val emblemType: String = ""
)

object CountryCatalog {
    val allCountries: List<Country> = listOf(
        // ================= NORTH & SOUTH AMERICAS =================
        Country("USA", "United States", "🇺🇸", "Americas", FlagPattern.STARS_AND_STRIPES, Color(0xFFB22234), Color(0xFFFFFFFF), Color(0xFF3C3B6E), emblemType = "stars"),
        Country("CAN", "Canada", "🇨🇦", "Americas", FlagPattern.CANADIAN_PALE, Color(0xFFFF0000), Color(0xFFFFFFFF), emblemType = "leaf"),
        Country("MEX", "Mexico", "🇲🇽", "Americas", FlagPattern.VERT_TRICOLOR, Color(0xFF006847), Color(0xFFFFFFFF), Color(0xFFCE1126), emblemType = "eagle"),
        Country("BRA", "Brazil", "🇧🇷", "Americas", FlagPattern.BRAZIL_RHOMBUS, Color(0xFF009C3B), Color(0xFFFFDF00), Color(0xFF002776)),
        Country("ARG", "Argentina", "🇦🇷", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFF74ACDF), Color(0xFFFFFFFF), Color(0xFF74ACDF), emblemType = "sun"),
        Country("COL", "Colombia", "🇨🇴", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFCD116), Color(0xFF003893), Color(0xFFCE1126)),
        Country("CHI", "Chile", "🇨🇱", "Americas", FlagPattern.HORIZ_BICOLOR, Color(0xFFFFFFFF), Color(0xFFD52B1E), Color(0xFF0039A6), emblemType = "star"),
        Country("URU", "Uruguay", "🇺🇾", "Americas", FlagPattern.GREECE_STRIPES, Color(0xFF0038A8), Color(0xFFFFFFFF), Color(0xFFFCD116), emblemType = "sun"),
        Country("PER", "Peru", "🇵🇪", "Americas", FlagPattern.VERT_TRICOLOR, Color(0xFFD91023), Color(0xFFFFFFFF), Color(0xFFD91023)),
        Country("VEN", "Venezuela", "🇻🇪", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFCE300), Color(0xFF00247D), Color(0xFFCF142B), emblemType = "stars"),
        Country("ECU", "Ecuador", "🇪🇨", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFFD100), Color(0xFF0033A0), Color(0xFFEF3340), emblemType = "condor"),
        Country("BOL", "Bolivia", "🇧🇴", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFFD52B1E), Color(0xFFFCD116), Color(0xFF007934)),
        Country("PAR", "Paraguay", "🇵🇾", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFFD52B1E), Color(0xFFFFFFFF), Color(0xFF0038A8), emblemType = "star"),
        Country("JAM", "Jamaica", "🇯🇲", "Americas", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF000000), Color(0xFF007749), Color(0xFFFFB81C)),
        Country("CUB", "Cuba", "🇨🇺", "Americas", FlagPattern.GREECE_STRIPES, Color(0xFF002A8F), Color(0xFFFFFFFF), Color(0xFFCB1515), emblemType = "star"),
        Country("CRC", "Costa Rica", "🇨🇷", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFF002B7F), Color(0xFFFFFFFF), Color(0xFFCE1126)),
        Country("PAN", "Panama", "🇵🇦", "Americas", FlagPattern.HORIZ_BICOLOR, Color(0xFFFFFFFF), Color(0xFFDA121A), Color(0xFF072357), emblemType = "star"),
        Country("GTM", "Guatemala", "🇬🇹", "Americas", FlagPattern.VERT_TRICOLOR, Color(0xFF4997D0), Color(0xFFFFFFFF), Color(0xFF4997D0), emblemType = "bird"),
        Country("HND", "Honduras", "🇭🇳", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFF0073CF), Color(0xFFFFFFFF), Color(0xFF0073CF), emblemType = "stars5"),
        Country("SLV", "El Salvador", "🇸🇻", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFF0047AB), Color(0xFFFFFFFF), Color(0xFF0047AB)),
        Country("NIC", "Nicaragua", "🇳🇮", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFF0067C6), Color(0xFFFFFFFF), Color(0xFF0067C6)),
        Country("DOM", "Dominican Republic", "🇩🇴", "Americas", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF002D62), Color(0xFFCE1126), Color(0xFFFFFFFF)),
        Country("HTI", "Haiti", "🇭🇹", "Americas", FlagPattern.HORIZ_BICOLOR, Color(0xFF00209F), Color(0xFFD21034)),
        Country("TTO", "Trinidad and Tobago", "🇹🇹", "Americas", FlagPattern.DIAGONAL_SALTIRE, Color(0xFFCE1126), Color(0xFF000000), Color(0xFFFFFFFF)),
        Country("BAH", "Bahamas", "🇧🇸", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFF00778B), Color(0xFFFFC72C), Color(0xFF000000)),
        Country("BRB", "Barbados", "🇧🇧", "Americas", FlagPattern.VERT_TRICOLOR, Color(0xFF00267F), Color(0xFFFFC72C), Color(0xFF000000), emblemType = "trident"),
        Country("GUY", "Guyana", "🇬🇾", "Americas", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF009E49), Color(0xFFFCD116), Color(0xFFCE1126)),
        Country("SUR", "Suriname", "🇸🇷", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFF377E3F), Color(0xFFB40A2D), Color(0xFFECC81D), emblemType = "star"),
        Country("BLZ", "Belize", "🇧🇿", "Americas", FlagPattern.HORIZ_TRICOLOR, Color(0xFFD91023), Color(0xFF003F87), Color(0xFFD91023)),

        // ================= EUROPE =================
        Country("FRA", "France", "🇫🇷", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF0055A4), Color(0xFFFFFFFF), Color(0xFFEF4135)),
        Country("GER", "Germany", "🇩🇪", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFF000000), Color(0xFFFF0000), Color(0xFFFFCC00)),
        Country("ESP", "Spain", "🇪🇸", "Europe", FlagPattern.SPAIN_CREST, Color(0xFFAA151B), Color(0xFFF1BF00), Color(0xFFAA151B)),
        Country("ITA", "Italy", "🇮🇹", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF009246), Color(0xFFFFFFFF), Color(0xFFCE2B37)),
        Country("GBR", "United Kingdom", "🇬🇧", "Europe", FlagPattern.CROSS_AND_SALTIRE, Color(0xFF012169), Color(0xFFC8102E), Color(0xFFFFFFFF)),
        Country("NED", "Netherlands", "🇳🇱", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFAE1C28), Color(0xFFFFFFFF), Color(0xFF21468B)),
        Country("POR", "Portugal", "🇵🇹", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF006600), Color(0xFFFF0000), Color(0xFFFFD700), emblemType = "shield"),
        Country("BEL", "Belgium", "🇧🇪", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF000000), Color(0xFFFFD100), Color(0xFFFF0F21)),
        Country("CRO", "Croatia", "🇭🇷", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFF0000), Color(0xFFFFFFFF), Color(0xFF171796), emblemType = "checker"),
        Country("SWE", "Sweden", "🇸🇪", "Europe", FlagPattern.NORDIC_CROSS, Color(0xFF006AA7), Color(0xFFFECC00)),
        Country("DEN", "Denmark", "🇩🇰", "Europe", FlagPattern.NORDIC_CROSS, Color(0xFFC60C30), Color(0xFFFFFFFF)),
        Country("NOR", "Norway", "🇳🇴", "Europe", FlagPattern.NORDIC_CROSS, Color(0xFFBA0C2F), Color(0xFF00205B), Color(0xFFFFFFFF)),
        Country("FIN", "Finland", "🇫🇮", "Europe", FlagPattern.NORDIC_CROSS, Color(0xFFFFFFFF), Color(0xFF003580)),
        Country("ISL", "Iceland", "🇮🇸", "Europe", FlagPattern.NORDIC_CROSS, Color(0xFF02529C), Color(0xFFDC1E35), Color(0xFFFFFFFF)),
        Country("SUI", "Switzerland", "🇨🇭", "Europe", FlagPattern.SOLID_EMBLEM, Color(0xFFFF0000), Color(0xFFFFFFFF), emblemType = "swiss_cross"),
        Country("POL", "Poland", "🇵🇱", "Europe", FlagPattern.HORIZ_BICOLOR, Color(0xFFFFFFFF), Color(0xFFDC143C)),
        Country("UKR", "Ukraine", "🇺🇦", "Europe", FlagPattern.HORIZ_BICOLOR, Color(0xFF0057B7), Color(0xFFFFD700)),
        Country("GRE", "Greece", "🇬🇷", "Europe", FlagPattern.GREECE_STRIPES, Color(0xFF0D5EAF), Color(0xFFFFFFFF), emblemType = "cross"),
        Country("AUT", "Austria", "🇦🇹", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFED2939), Color(0xFFFFFFFF), Color(0xFFED2939)),
        Country("IRL", "Ireland", "🇮🇪", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF169B62), Color(0xFFFFFFFF), Color(0xFFFF883E)),
        Country("CZE", "Czech Republic", "🇨🇿", "Europe", FlagPattern.HORIZ_BICOLOR, Color(0xFFFFFFFF), Color(0xFFD7141A), Color(0xFF11457E)),
        Country("ROU", "Romania", "🇷🇴", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF002B7F), Color(0xFFFCD116), Color(0xFFCE1126)),
        Country("HUN", "Hungary", "🇭🇺", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCE2939), Color(0xFFFFFFFF), Color(0xFF477050)),
        Country("SRB", "Serbia", "🇷🇸", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFC6363C), Color(0xFF0C4076), Color(0xFFFFFFFF)),
        Country("SVK", "Slovakia", "🇸🇰", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFFFFFF), Color(0xFF0B4EA2), Color(0xFFEE1C25)),
        Country("BGR", "Bulgaria", "🇧🇬", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFFFFFF), Color(0xFF00966E), Color(0xFFD62612)),
        Country("BLR", "Belarus", "🇧🇾", "Europe", FlagPattern.HORIZ_BICOLOR, Color(0xFFC8313E), Color(0xFF009E60)),
        Country("LUX", "Luxembourg", "🇱🇺", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFEA141D), Color(0xFFFFFFFF), Color(0xFF00A1DE)),
        Country("EST", "Estonia", "🇪🇪", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFF0072CE), Color(0xFF000000), Color(0xFFFFFFFF)),
        Country("LVA", "Latvia", "🇱🇻", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFF9E3039), Color(0xFFFFFFFF), Color(0xFF9E3039)),
        Country("LTU", "Lithuania", "🇱🇹", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFDB913), Color(0xFF006A44), Color(0xFFC1272D)),
        Country("SVN", "Slovenia", "🇸🇮", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFFFFFF), Color(0xFF005DA4), Color(0xFFED1C24)),
        Country("CYP", "Cyprus", "🇨🇾", "Europe", FlagPattern.SOLID_EMBLEM, Color(0xFFFFFFFF), Color(0xFFD57800)),
        Country("MLT", "Malta", "🇲🇹", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFFFFFFFF), Color(0xFFCF142B)),
        Country("ALB", "Albania", "🇦🇱", "Europe", FlagPattern.SOLID_EMBLEM, Color(0xFFE41E20), Color(0xFF000000), emblemType = "eagle"),
        Country("BIH", "Bosnia and Herzegovina", "🇧🇦", "Europe", FlagPattern.SOLID_EMBLEM, Color(0xFF002395), Color(0xFFFECB00), Color(0xFFFFFFFF), emblemType = "stars"),
        Country("MKD", "North Macedonia", "🇲🇰", "Europe", FlagPattern.SOLID_EMBLEM, Color(0xFFD20000), Color(0xFFFFE600), emblemType = "sun"),
        Country("MNE", "Montenegro", "🇲🇪", "Europe", FlagPattern.SOLID_EMBLEM, Color(0xFFC40308), Color(0xFFD4AF37), emblemType = "eagle"),
        Country("MDA", "Moldova", "🇲🇩", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF003DA5), Color(0xFFFFD100), Color(0xFFC8102E)),
        Country("GEO", "Georgia", "🇬🇪", "Europe", FlagPattern.SOLID_EMBLEM, Color(0xFFFFFFFF), Color(0xFFFF0000), emblemType = "crosses"),
        Country("ARM", "Armenia", "🇦🇲", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFFD90012), Color(0xFF0033A0), Color(0xFFF2A800)),
        Country("AZE", "Azerbaijan", "🇦🇿", "Europe", FlagPattern.HORIZ_TRICOLOR, Color(0xFF00B5E2), Color(0xFFEF3340), Color(0xFF509E2F), emblemType = "crescent"),
        Country("AND", "Andorra", "🇦🇩", "Europe", FlagPattern.VERT_TRICOLOR, Color(0xFF10069F), Color(0xFFFED100), Color(0xFFD50032)),
        Country("MCO", "Monaco", "🇲🇨", "Europe", FlagPattern.HORIZ_BICOLOR, Color(0xFFCE1126), Color(0xFFFFFFFF)),
        Country("SMR", "San Marino", "🇸🇲", "Europe", FlagPattern.HORIZ_BICOLOR, Color(0xFFFFFFFF), Color(0xFF5EB6E4)),
        Country("LIE", "Liechtenstein", "🇱🇮", "Europe", FlagPattern.HORIZ_BICOLOR, Color(0xFF002B7F), Color(0xFFCE1126), emblemType = "crown"),

        // ================= ASIA =================
        Country("CHN", "China", "🇨🇳", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFFDE2910), Color(0xFFFFDE00), emblemType = "stars5"),
        Country("JPN", "Japan", "🇯🇵", "Asia", FlagPattern.CENTER_CIRCLE, Color(0xFFFFFFFF), Color(0xFFBC002D)),
        Country("IND", "India", "🇮🇳", "Asia", FlagPattern.INDIA_CHAKRA, Color(0xFFFF9933), Color(0xFFFFFFFF), Color(0xFF138808), emblemType = "chakra"),
        Country("KOR", "South Korea", "🇰🇷", "Asia", FlagPattern.KOREA_TAEGEUK, Color(0xFFFFFFFF), Color(0xFFCD2E3A), Color(0xFF0047A0), emblemType = "taegeuk"),
        Country("IDN", "Indonesia", "🇮🇩", "Asia", FlagPattern.HORIZ_BICOLOR, Color(0xFFFF0000), Color(0xFFFFFFFF)),
        Country("KSA", "Saudi Arabia", "🇸🇦", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFF006C35), Color(0xFFFFFFFF), emblemType = "sword"),
        Country("TUR", "Turkey", "🇹🇷", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFFE30A17), Color(0xFFFFFFFF), emblemType = "crescent"),
        Country("VIE", "Vietnam", "🇻🇳", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFFDA251D), Color(0xFFFFEB3B), emblemType = "star"),
        Country("THA", "Thailand", "🇹🇭", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFA51931), Color(0xFFF4F5F8), Color(0xFF2D2A4A)),
        Country("SGP", "Singapore", "🇸🇬", "Asia", FlagPattern.HORIZ_BICOLOR, Color(0xFFED2939), Color(0xFFFFFFFF), emblemType = "crescent"),
        Country("PHI", "Philippines", "🇵🇭", "Asia", FlagPattern.HORIZ_BICOLOR, Color(0xFF0038A8), Color(0xFFCE1126), Color(0xFFFFFFFF), emblemType = "sun"),
        Country("MYS", "Malaysia", "🇲🇾", "Asia", FlagPattern.STARS_AND_STRIPES, Color(0xFFCC0000), Color(0xFFFFFFFF), Color(0xFF000066), emblemType = "crescent"),
        Country("PAK", "Pakistan", "🇵🇰", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFF01411C), Color(0xFFFFFFFF), emblemType = "crescent"),
        Country("BGD", "Bangladesh", "🇧🇩", "Asia", FlagPattern.CENTER_CIRCLE, Color(0xFF006A4E), Color(0xFFF42A41)),
        Country("IRN", "Iran", "🇮🇷", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFF239F40), Color(0xFFFFFFFF), Color(0xFFDA0000)),
        Country("IRQ", "Iraq", "🇮🇶", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCE1126), Color(0xFFFFFFFF), Color(0xFF000000)),
        Country("ARE", "United Arab Emirates", "🇦🇪", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFF00732F), Color(0xFFFFFFFF), Color(0xFF000000), color4 = Color(0xFFFF0000)),
        Country("ISR", "Israel", "🇮🇱", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFFFFFF), Color(0xFF0038B8), Color(0xFFFFFFFF), emblemType = "star_david"),
        Country("QAT", "Qatar", "🇶🇦", "Asia", FlagPattern.VERT_TRICOLOR, Color(0xFFFFFFFF), Color(0xFF8A1538)),
        Country("KWT", "Kuwait", "🇰🇼", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFF007A3D), Color(0xFFFFFFFF), Color(0xFFCE1126)),
        Country("OMN", "Oman", "🇴🇲", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFDB161B), Color(0xFFFFFFFF), Color(0xFF008000)),
        Country("JOR", "Jordan", "🇯🇴", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFF000000), Color(0xFFFFFFFF), Color(0xFF007A3D)),
        Country("LBN", "Lebanon", "🇱🇧", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFED1C24), Color(0xFFFFFFFF), Color(0xFFED1C24), emblemType = "cedar"),
        Country("KAZ", "Kazakhstan", "🇰🇿", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFF00AFCA), Color(0xFFFEC50C), emblemType = "sun_eagle"),
        Country("UZB", "Uzbekistan", "🇺🇿", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFF0099B5), Color(0xFFFFFFFF), Color(0xFF1EB53A)),
        Country("LKA", "Sri Lanka", "🇱🇰", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFF8D153A), Color(0xFFFFBE29), Color(0xFFFF7900), emblemType = "lion"),
        Country("NPL", "Nepal", "🇳🇵", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFFDC143C), Color(0xFF003893), Color(0xFFFFFFFF), emblemType = "sun"),
        Country("MMR", "Myanmar", "🇲🇲", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFFECB00), Color(0xFF34B233), Color(0xFFEA2839), emblemType = "star"),
        Country("KHM", "Cambodia", "🇰🇭", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFF032EA6), Color(0xFFED1B24), Color(0xFF032EA6), emblemType = "temple"),
        Country("MNG", "Mongolia", "🇲🇳", "Asia", FlagPattern.VERT_TRICOLOR, Color(0xFFDA2032), Color(0xFF0066B3), Color(0xFFDA2032)),
        Country("PRK", "North Korea", "🇰🇵", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFF024FA2), Color(0xFFED1B2C), Color(0xFF024FA2), emblemType = "star"),
        Country("AFG", "Afghanistan", "🇦🇫", "Asia", FlagPattern.VERT_TRICOLOR, Color(0xFF000000), Color(0xFFD32011), Color(0xFF007A36)),
        Country("SYR", "Syria", "🇸🇾", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCE1126), Color(0xFFFFFFFF), Color(0xFF000000), emblemType = "stars"),
        Country("YEM", "Yemen", "🇾🇪", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCE1126), Color(0xFFFFFFFF), Color(0xFF000000)),
        Country("BHR", "Bahrain", "🇧🇭", "Asia", FlagPattern.VERT_TRICOLOR, Color(0xFFFFFFFF), Color(0xFFDA291C)),
        Country("MDV", "Maldives", "🇲🇻", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFFD21034), Color(0xFF007E3A), Color(0xFFFFFFFF), emblemType = "crescent"),
        Country("BRN", "Brunei", "🇧🇳", "Asia", FlagPattern.DIAGONAL_SALTIRE, Color(0xFFF7E017), Color(0xFFFFFFFF), Color(0xFF000000)),
        Country("TJK", "Tajikistan", "🇹🇯", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCC0000), Color(0xFFFFFFFF), Color(0xFF006600)),
        Country("KGZ", "Kyrgyzstan", "🇰🇬", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFFE8112D), Color(0xFFFFEF00), emblemType = "sun"),
        Country("TKM", "Turkmenistan", "🇹🇲", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFF2E8540), Color(0xFFFFFFFF), Color(0xFFD22630), emblemType = "crescent"),
        Country("LAO", "Laos", "🇱🇦", "Asia", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCE1126), Color(0xFF002868), Color(0xFFCE1126), emblemType = "moon"),
        Country("TLS", "Timor-Leste", "🇹🇱", "Asia", FlagPattern.SOLID_EMBLEM, Color(0xFFDC241F), Color(0xFFFFC726), Color(0xFF000000)),
        Country("BTN", "Bhutan", "🇧🇹", "Asia", FlagPattern.DIAGONAL_SALTIRE, Color(0xFFFFD520), Color(0xFFFF4E12), emblemType = "dragon"),

        // ================= AFRICA =================
        Country("RSA", "South Africa", "🇿🇦", "Africa", FlagPattern.SOUTH_AFRICA_Y, Color(0xFF007749), Color(0xFF001489), Color(0xFFE03C31)),
        Country("EGY", "Egypt", "🇪🇬", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCE1126), Color(0xFFFFFFFF), Color(0xFF000000), emblemType = "eagle"),
        Country("NGR", "Nigeria", "🇳🇬", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFF008751), Color(0xFFFFFFFF), Color(0xFF008751)),
        Country("MAR", "Morocco", "🇲🇦", "Africa", FlagPattern.SOLID_EMBLEM, Color(0xFFC1272D), Color(0xFF006233), emblemType = "star"),
        Country("GHA", "Ghana", "🇬🇭", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFFCE1126), Color(0xFFFCD116), Color(0xFF006B3F), emblemType = "star"),
        Country("SEN", "Senegal", "🇸🇳", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFF00853F), Color(0xFFFDEF42), Color(0xFFE31B23), emblemType = "star"),
        Country("KEN", "Kenya", "🇰🇪", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF000000), Color(0xFFBB0000), Color(0xFF006600), emblemType = "shield"),
        Country("ETH", "Ethiopia", "🇪🇹", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF078930), Color(0xFFFCDD09), Color(0xFFDA121A), emblemType = "star"),
        Country("DZA", "Algeria", "🇩🇿", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFF006233), Color(0xFFFFFFFF), Color(0xFFD21034), emblemType = "crescent"),
        Country("CMR", "Cameroon", "🇨🇲", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFF007A5E), Color(0xFFCE1126), Color(0xFFFCD116), emblemType = "star"),
        Country("CIV", "Ivory Coast", "🇨🇮", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFFF77F00), Color(0xFFFFFFFF), Color(0xFF009E60)),
        Country("TUN", "Tunisia", "🇹🇳", "Africa", FlagPattern.SOLID_EMBLEM, Color(0xFFE70013), Color(0xFFFFFFFF), emblemType = "crescent"),
        Country("UGA", "Uganda", "🇺🇬", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF000000), Color(0xFFFCDC04), Color(0xFFD90000), emblemType = "crane"),
        Country("TZA", "Tanzania", "🇹🇿", "Africa", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF1EB53A), Color(0xFF00A3DD), Color(0xFF000000)),
        Country("ZWE", "Zimbabwe", "🇿🇼", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF006400), Color(0xFFFFD200), Color(0xFFD40000), emblemType = "bird"),
        Country("AGO", "Angola", "🇦🇴", "Africa", FlagPattern.HORIZ_BICOLOR, Color(0xFFCC092F), Color(0xFF000000), Color(0xFFFFD100)),
        Country("MOZ", "Mozambique", "🇲🇿", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF006600), Color(0xFF000000), Color(0xFFFFD100)),
        Country("ZMB", "Zambia", "🇿🇲", "Africa", FlagPattern.SOLID_EMBLEM, Color(0xFF198A00), Color(0xFFDE2010), Color(0xFFEF7D00)),
        Country("RWA", "Rwanda", "🇷🇼", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF00A1DE), Color(0xFFFAD201), Color(0xFF20603D), emblemType = "sun"),
        Country("COD", "DR Congo", "🇨🇩", "Africa", FlagPattern.SOLID_EMBLEM, Color(0xFF007FFF), Color(0xFFCE1021), Color(0xFFF7D618), emblemType = "star"),
        Country("COG", "Republic of the Congo", "🇨🇬", "Africa", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF009543), Color(0xFFFBDE4A), Color(0xFFDC241F)),
        Country("SDN", "Sudan", "🇸🇩", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFFD21034), Color(0xFFFFFFFF), Color(0xFF000000)),
        Country("SSD", "South Sudan", "🇸🇸", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF000000), Color(0xFFE11919), Color(0xFF078930)),
        Country("MDG", "Madagascar", "🇲🇬", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFFFFFFFF), Color(0xFFFC3D32), Color(0xFF007E3A)),
        Country("MLI", "Mali", "🇲🇱", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFF14B53A), Color(0xFFFCD116), Color(0xFFCE1126)),
        Country("BFA", "Burkina Faso", "🇧🇫", "Africa", FlagPattern.HORIZ_BICOLOR, Color(0xFFEF2B2D), Color(0xFF009E49), Color(0xFFFCD116), emblemType = "star"),
        Country("NER", "Niger", "🇳🇪", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFFE05206), Color(0xFFFFFFFF), Color(0xFF0DB02B), emblemType = "sun"),
        Country("TCD", "Chad", "🇹🇩", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFF002664), Color(0xFFFECB00), Color(0xFFC60C30)),
        Country("GIN", "Guinea", "🇬🇳", "Africa", FlagPattern.VERT_TRICOLOR, Color(0xFFCE1126), Color(0xFFFCD116), Color(0xFF009460)),
        Country("BEN", "Benin", "🇧🇯", "Africa", FlagPattern.HORIZ_BICOLOR, Color(0xFF008751), Color(0xFFFCD116), Color(0xFFE8112D)),
        Country("TGO", "Togo", "🇹🇬", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF006A4E), Color(0xFFFFCE00), Color(0xFFD21034), emblemType = "star"),
        Country("SLE", "Sierra Leone", "🇸🇱", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF1EB53A), Color(0xFFFFFFFF), Color(0xFF0072CE)),
        Country("LBR", "Liberia", "🇱🇷", "Africa", FlagPattern.STARS_AND_STRIPES, Color(0xFFBF0A30), Color(0xFFFFFFFF), Color(0xFF002868), emblemType = "star"),
        Country("MRT", "Mauritania", "🇲🇷", "Africa", FlagPattern.SOLID_EMBLEM, Color(0xFF006233), Color(0xFFFFD700), Color(0xFFD01C1F), emblemType = "crescent"),
        Country("SOM", "Somalia", "🇸🇴", "Africa", FlagPattern.SOLID_EMBLEM, Color(0xFF4189DD), Color(0xFFFFFFFF), emblemType = "star"),
        Country("LBY", "Libya", "🇱🇾", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFFE70013), Color(0xFF000000), Color(0xFF239E46), emblemType = "crescent"),
        Country("NAM", "Namibia", "🇳🇦", "Africa", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF003580), Color(0xFFD21034), Color(0xFF009543), emblemType = "sun"),
        Country("BWA", "Botswana", "🇧🇼", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF75AADB), Color(0xFF000000), Color(0xFFFFFFFF)),
        Country("GAB", "Gabon", "🇬🇦", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF009E60), Color(0xFFFCD116), Color(0xFF3A75C4)),
        Country("MUS", "Mauritius", "🇲🇺", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFFEA2839), Color(0xFF1A2061), Color(0xFFFFD500)),
        Country("SYC", "Seychelles", "🇸🇨", "Africa", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF003D88), Color(0xFFFCD856), Color(0xFFD62828)),
        Country("CPV", "Cape Verde", "🇨🇻", "Africa", FlagPattern.HORIZ_TRICOLOR, Color(0xFF003893), Color(0xFFCF2027), Color(0xFFFFFFFF), emblemType = "stars"),

        // ================= OCEANIA =================
        Country("AUS", "Australia", "🇦🇺", "Oceania", FlagPattern.SOLID_EMBLEM, Color(0xFF00008B), Color(0xFFFFFFFF), emblemType = "southern_cross"),
        Country("NZL", "New Zealand", "🇳🇿", "Oceania", FlagPattern.SOLID_EMBLEM, Color(0xFF00247D), Color(0xFFCC142B), emblemType = "red_stars"),
        Country("FJI", "Fiji", "🇫🇯", "Oceania", FlagPattern.SOLID_EMBLEM, Color(0xFF68BFE5), Color(0xFFFFFFFF), Color(0xFFCC142B), emblemType = "shield"),
        Country("PNG", "Papua New Guinea", "🇵🇬", "Oceania", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF000000), Color(0xFFCE1126), Color(0xFFFFCE00), emblemType = "bird"),
        Country("WSM", "Samoa", "🇼🇸", "Oceania", FlagPattern.SOLID_EMBLEM, Color(0xFFCE1126), Color(0xFF002B7F), Color(0xFFFFFFFF), emblemType = "stars5"),
        Country("TON", "Tonga", "🇹🇴", "Oceania", FlagPattern.SOLID_EMBLEM, Color(0xFFC10000), Color(0xFFFFFFFF), emblemType = "cross"),
        Country("VUT", "Vanuatu", "🇻🇺", "Oceania", FlagPattern.HORIZ_BICOLOR, Color(0xFFD21034), Color(0xFF009543), Color(0xFF000000)),
        Country("SLB", "Solomon Islands", "🇸🇧", "Oceania", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF0051BA), Color(0xFF215B33), Color(0xFFFFCE00)),
        Country("PLW", "Palau", "🇵🇼", "Oceania", FlagPattern.CENTER_CIRCLE, Color(0xFF4AADD6), Color(0xFFFFDE00)),
        Country("FSM", "Micronesia", "🇫🇲", "Oceania", FlagPattern.SOLID_EMBLEM, Color(0xFF003CE7), Color(0xFFFFFFFF), emblemType = "stars"),
        Country("KIR", "Kiribati", "🇰🇮", "Oceania", FlagPattern.HORIZ_BICOLOR, Color(0xFFCE1126), Color(0xFF00247D), Color(0xFFFCD116)),
        Country("NRU", "Nauru", "🇳🇷", "Oceania", FlagPattern.HORIZ_TRICOLOR, Color(0xFF002B7F), Color(0xFFFFC72C), Color(0xFFFFFFFF), emblemType = "star"),
        Country("TUV", "Tuvalu", "🇹🇻", "Oceania", FlagPattern.SOLID_EMBLEM, Color(0xFF5B97C8), Color(0xFFFFCE00), emblemType = "stars"),
        Country("MHL", "Marshall Islands", "🇲🇭", "Oceania", FlagPattern.DIAGONAL_SALTIRE, Color(0xFF0038A8), Color(0xFFDD7500), Color(0xFFFFFFFF), emblemType = "star")
    )

    fun getByCode(code: String): Country? {
        return allCountries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }

    val presets = mapOf(
        "All Countries Mega" to allCountries.map { it.code },
        "World Cup Titans" to listOf("BRA", "ARG", "FRA", "GER", "ESP", "ITA", "GBR", "NED", "POR", "CRO", "URU", "BEL"),
        "Global G20" to listOf("USA", "CHN", "JPN", "GER", "IND", "GBR", "FRA", "BRA", "ITA", "CAN", "KOR", "AUS", "MEX", "IDN", "KSA", "TUR", "RSA", "ARG"),
        "Euro Championship" to listOf("FRA", "GER", "ESP", "ITA", "GBR", "NED", "POR", "BEL", "CRO", "SWE", "DEN", "SUI", "POL", "UKR", "GRE", "NOR", "FIN", "AUT", "IRL", "CZE"),
        "Americas Derby" to listOf("USA", "BRA", "ARG", "MEX", "CAN", "COL", "CHI", "URU", "JAM", "PER", "VEN", "ECU", "BOL", "PAR", "CUB", "CRC"),
        "Asia & Pacific Clash" to listOf("JPN", "KOR", "IND", "CHN", "AUS", "IDN", "VIE", "THA", "SGP", "PHI", "MYS", "PAK", "BGD", "KSA", "NZL", "TUR", "IRN"),
        "Africa Nations" to listOf("RSA", "EGY", "NGR", "MAR", "GHA", "SEN", "KEN", "ETH", "DZA", "CMR", "CIV", "TUN", "UGA", "TZA", "ZWE"),
        "Oceania Showdown" to listOf("AUS", "NZL", "FJI", "PNG", "WSM", "TON", "VUT", "SLB", "PLW", "KIR"),
        "Quick 8 Chaos" to listOf("USA", "BRA", "JPN", "GER", "GBR", "IND", "FRA", "ARG")
    )
}
