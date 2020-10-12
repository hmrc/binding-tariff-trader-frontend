/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package service

import models.Country
import play.api.i18n.Messages
import play.api.libs.json.JsObject

class CountriesService  {

  def getAllCountries: List[Country] = countries

  def getAllCountriesById: Map[String, Country] = countriesById

  def autoCompleteSynonymCountries(implicit messages: Messages): JsObject = {
      countries.flatMap(country => country.toNewAutoCompleteJson)
        .foldLeft(JsObject.empty){(countryList, c) => countryList + (c) }
  }

  private val countries = List(
    Country("AE0", "title.abu_dhabi", "AE0", Nil),
    Country("AF", "title.afghanistan", "AF", Nil),
    Country("AE1", "title.ajman", "AE1", Nil),
    Country("AX", "title.aland_islands", "AX", List("Aland Islands")),
    Country("US0", "title.alaska", "US0", Nil),
    Country("AL", "title.albania", "AL", Nil),
    Country("DZ", "title.algeria", "DZ", Nil),
    Country("UM0", "title.american_polynesia_and_oceania", "UM0", Nil),
    Country("AS", "title.american_samoa", "AS", Nil),
    Country("AD", "title.andorra", "AD", Nil),
    Country("AO", "title.angola", "AO", Nil),
    Country("AI", "title.anguilla", "AI", Nil),
    Country("AQ0", "title.antarctica", "AQ0", List("Antartica")),
    Country("AG", "title.antigua_and_barbuda", "AG", Nil),
    Country("AR", "title.argentina", "AR", Nil),
    Country("AM", "title.armenia", "AM", Nil),
    Country("AW", "title.aruba", "AW", Nil),
    Country("SH", "title.ascension", "SH", Nil),
    Country("AU", "title.australia", "AU", List("Oz")),
    Country("AQ1", "title.australian_antarctic_territory", "AQ1", Nil),
    Country("AT", "title.austria", "AT", Nil),
    Country("AZ", "title.azerbaijan", "AZ", Nil),
    Country("PT1", "title.azores", "PT1", Nil),
    Country("BS", "title.bahamas", "BS", Nil),
    Country("BH", "title.bahrain", "BH", Nil),
    Country("UM1", "title.baker_island", "UM1", Nil),
    Country("BD", "title.bangladesh", "BD", Nil),
    Country("BB", "title.barbados", "BB", Nil),
    Country("BY", "title.belarus", "BY", Nil),
    Country("BE", "title.belgium", "BE", Nil),
    Country("BZ", "title.belize", "BZ", Nil),
    Country("BJ", "title.benin", "BJ", Nil),
    Country("BM", "title.bermuda", "BM", Nil),
    Country("BT", "title.bhutan", "BT", Nil),
    Country("BO", "title.bolivia", "BO", Nil),
    Country("BQ0", "title.bonaire", "BQ0", Nil),
    Country("BA", "title.bosnia_and_herzegovina", "BA", Nil),
    Country("BW", "title.botswana", "BW", Nil),
    Country("BV", "title.bouvet_island", "BV", Nil),
    Country("BR", "title.brazil", "BR", Nil),
    Country("AQ2", "title.british_antarctic_territory", "AQ2", Nil),
    Country("IO", "title.british_indian_ocean_territory", "IO", List("BIOT")),
    Country("VG", "title.british_virgin_islands", "VG", Nil),
    Country("BN", "title.brunei", "BN", Nil),
    Country("BG", "title.bulgaria", "BG", Nil),
    Country("BF", "title.burkina_faso", "BF", Nil),
    Country("BI", "title.burundi", "BI", Nil),
    Country("KH", "title.cambodia", "KH", Nil),
    Country("CM", "title.cameroon", "CM", Nil),
    Country("CA", "title.canada", "CA", Nil),
    Country("ES", "title.canary_islands", "ES", List("Canaries")),
    Country("CV", "title.cape_verde", "CV", List("Republic of Cabo Verde")),
    Country("UM2", "title.caroline_islands", "UM2", Nil),
    Country("KY", "title.cayman_islands", "KY", List("Caymans")),
    Country("CF", "title.central_african_republic", "CF", List("CAR")),
    Country("XC", "title.ceuta", "XC", Nil),
    Country("TD", "title.chad", "TD", Nil),
    Country("CL", "title.chile", "CL", Nil),
    Country("CN", "title.china", "CN", List("People's Republic of China", "PRC", "Peoples Republic of China")),
    Country("CX", "title.christmas_island", "CX", List("Christmas Islands")),
    Country("CC0", "title.cocos_islands", "CC0", Nil),
    Country("CO", "title.colombia", "CO", List("Columbia")),
    Country("KM", "title.comoros", "KM", Nil),
    Country("CD", "title.democratic_republic_of_the_congo", "CD", List("DR Congo", "DRC")),
    Country("CG", "title.congo", "CG", List("Congo-Brazzaville", "Congo Republic")),
    Country("CK", "title.cook_islands", "CK", Nil),
    Country("NI0", "title.corn_islands", "NI0", Nil),
    Country("CR", "title.costa_rica", "CR", Nil),
    Country("CI0", "title.cote_divoire", "CI0", List("Ivory Coast", "Cote d'Ivoire", "Cote dIvoire", "Cote d Ivoire")),
    Country("HR", "title.croatia", "HR", Nil),
    Country("CU", "title.cuba", "CU", Nil),
    Country("CW", "title.curacao", "CW", List("Curacao", "Curacoa")),
    Country("CY", "title.cyprus", "CY", Nil),
    Country("CZ", "title.czech_republic", "CZ", List("Czechoslovakia", "Czechia")),
    Country("DK", "title.denmark", "DK", List("Danish")),
    Country("GP0", "title.desirade", "GP0", Nil),
    Country("DJ", "title.djibouti", "DJ", Nil),
    Country("DM", "title.dominica", "DM", Nil),
    Country("DO", "title.dominican_republic", "DO", Nil),
    Country("AE2", "title.dubai", "AE2", Nil),
    Country("TL", "title.e_east_timor", "TL", Nil),
    Country("EC", "title.ecuador", "EC", Nil),
    Country("EG", "title.egypt", "EG", Nil),
    Country("SV", "title.el_salvador", "SV", Nil),
    Country("GQ", "title.equatorial_guinea", "GQ", Nil),
    Country("ER", "title.eritrea", "ER", Nil),
    Country("EE", "title.estonia", "EE", Nil),
    Country("ET", "title.ethiopia", "ET", Nil),
    Country("FK", "title.falkland_islands", "FK", List("Falklands")),
    Country("FO", "title.faroe_islands", "FO", List("Faroes")),
    Country("FJ", "title.fiji", "FJ", Nil),
    Country("FI", "title.finland", "FI", Nil),
    Country("FR", "title.france", "FR", Nil),
    Country("AQ3", "title.french_antarctic_territory", "AQ3", Nil),
    Country("GF", "title.french_guiana", "GF", List("French Guayana")),
    Country("PF", "title.french_polynesia", "PF", List("Polynesian Islands")),
    Country("TF", "title.french_southern_territories", "TF", Nil),
    Country("AE3", "title.fujairah", "AE3", Nil),
    Country("GA", "title.gabon", "GA", Nil),
    Country("GM", "title.gambia", "GM", Nil),
    Country("PS0", "title.gaza_and_jericho", "PS0", Nil),
    Country("GE", "title.georgia", "GE", Nil),
    Country("DE", "title.germany", "DE", List("Deutschland")),
    Country("GH", "title.ghana", "GH", Nil),
    Country("GI", "title.gibraltar", "GI", List("Gibraltar Rock")),
    Country("SH2", "title.gough", "SH2", Nil),
    Country("GR", "title.greece", "GR", Nil),
    Country("GL", "title.greenland", "GL", Nil),
    Country("GD", "title.grenada", "GD", Nil),
    Country("GP", "title.guadeloupe", "GP", Nil),
    Country("GU", "title.guam", "GU", Nil),
    Country("GT", "title.guatemala", "GT", Nil),
    Country("GG", "title.guernsey", "GG", List("Channel Islands")),
    Country("GN", "title.guinea", "GN", Nil),
    Country("GW", "title.guinea_bissau", "GW", Nil),
    Country("GY", "title.guyana", "GY", Nil),
    Country("HT", "title.haiti", "HT", Nil),
    Country("US1", "title.hawaii", "US1", Nil),
    Country("HM", "title.heard_and_mcdonald_islands", "HM", Nil),
    Country("HN", "title.honduras", "HN", Nil),
    Country("HK", "title.hong_kong", "HK", Nil),
    Country("UM3", "title.howland_islands", "UM3", Nil),
    Country("HU", "title.hungary", "HU", Nil),
    Country("IS", "title.iceland", "IS", Nil),
    Country("GP1", "title.lles_des_saintes", "GP1", Nil),
    Country("IN", "title.india", "IN", Nil),
    Country("ID", "title.indonesia", "ID", Nil),
    Country("IR", "title.iran", "IR", Nil),
    Country("IQ", "title.iraq", "IQ", Nil),
    Country("IE", "title.irish_republic", "IE", List("Republic of Ireland", "Eire")),
    Country("IL", "title.israel", "IL", Nil),
    Country("IT", "title.italy", "IT", Nil),
    Country("CI1", "title.ivory_coast", "CI1", Nil),
    Country("JM", "title.jamaica", "JM", Nil),
    Country("UM4", "title.jarvis_islands", "UM4", Nil),
    Country("JP", "title.japan", "JP", Nil),
    Country("JE", "title.jersey", "JE", List("Channel Islands")),
    Country("UM5", "title.johnston_atoll", "UM5", Nil),
    Country("JO", "title.jordan", "JO", Nil),
    Country("KZ", "title.kazakhstan", "KZ", Nil),
    Country("CC1", "title.keeling_islands", "CC1", Nil),
    Country("KE", "title.kenya", "KE", Nil),
    Country("UM6", "title.kingman_reef", "UM6", Nil),
    Country("KI", "title.kiribati", "KI", Nil),
    Country("KP", "title.north_korea", "KP", Nil),
    Country("KR", "title.south_korea", "KR", Nil),
    Country("XK", "title.kosovo", "XK", Nil),
    Country("KW", "title.kuwait", "KW", Nil),
    Country("KG", "title.kyrgyzstan", "KG", Nil),
    Country("LA", "title.lao_peoples_democratic_republic", "LA", List("Lao Peoples Democratic Republic")),
    Country("LV", "title.latvia", "LV", Nil),
    Country("LB", "title.lebanon", "LB", Nil),
    Country("LS", "title.lesotho", "LS", Nil),
    Country("LR", "title.liberia", "LR", Nil),
    Country("LY", "title.libya", "LY", Nil),
    Country("LI", "title.liechtenstein", "LI", Nil),
    Country("LT", "title.lithuania", "LT", Nil),
    Country("LU", "title.luxembourg", "LU", List("Luxemburg")),
    Country("MO", "title.macao", "MO", Nil),
    Country("MK", "title.macedonia", "MK", Nil),
    Country("MG", "title.madagascar", "MG", Nil),
    Country("PT0", "title.madeira", "PT0", Nil),
    Country("MW", "title.malawi", "MW", Nil),
    Country("MY", "title.malaysia", "MY", Nil),
    Country("MV", "title.maldives", "MV", List("Maldive Islands")),
    Country("ML", "title.mali", "ML", List("Mali Republic", "Republic of Mali")),
    Country("MT", "title.malta", "MT", Nil),
    Country("IM", "title.man_of_isle", "IM", Nil),
    Country("GP2", "title.maria_galante", "GP2", Nil),
    Country("MH", "title.marshall_islands", "MH", Nil),
    Country("MQ", "title.martinique", "MQ", Nil),
    Country("MR", "title.mauritania", "MR", Nil),
    Country("MU", "title.mauritius", "MU", Nil),
    Country("YT", "title.mayotte", "YT", Nil),
    Country("XL", "title.melilla", "XL", Nil),
    Country("MX", "title.mexico", "MX", Nil),
    Country("FM", "title.micronesia", "FM", Nil),
    Country("UM7", "title.midway_island", "UM7", Nil),
    Country("MD", "title.moldova", "MD", Nil),
    Country("FR0", "title.monaco", "FR0", Nil),
    Country("MN", "title.mongolia", "MN", Nil),
    Country("ME", "title.montenegro", "ME", Nil),
    Country("MS", "title.montserrat", "MS", Nil),
    Country("MA", "title.morocco", "MA", Nil),
    Country("MZ", "title.mozambique", "MZ", Nil),
    Country("MM", "title.myanmar", "MM", Nil),
    Country("NA", "title.namibia", "NA", Nil),
    Country("NR", "title.nauru", "NR", Nil),
    Country("NP", "title.nepal", "NP", Nil),
    Country("NL", "title.netherlands", "NL", List("Holland", "Dutch", "Amsterdam")),
    Country("NC", "title.new_caledonia", "NC", Nil),
    Country("NZ", "title.new_zealand", "NZ", Nil),
    Country("NI", "title.nicaragua", "NI", Nil),
    Country("NE", "title.niger", "NE", List("Republic of the Niger", "Niger Republic")),
    Country("NG", "title.nigeria", "NG", Nil),
    Country("NU", "title.niue", "NU", Nil),
    Country("NF", "title.norfolk_island", "NF", Nil),
    Country("MP", "title.northern_mariana_islands", "MP", Nil),
    Country("NO", "title.norway", "NO", Nil),
    Country("PS", "title.occupied_palestinian_territory", "PS", Nil),
    Country("OM", "title.oman", "OM", Nil),
    Country("PK", "title.pakistan", "PK", Nil),
    Country("UM8", "title.palra_island", "UM8", Nil),
    Country("PW", "title.palau", "PW", Nil),
    Country("PA", "title.panama", "PA", Nil),
    Country("PG", "title.papua_new_guinea", "PG", Nil),
    Country("PY", "title.paraguay", "PY", Nil),
    Country("PE", "title.peru", "PE", Nil),
    Country("PH", "title.philippines", "PH", List("Philippenes", "Phillipines", "Phillippines", "Philipines")),
    Country("PN", "title.pitcairn", "PN", Nil),
    Country("PL", "title.poland", "PL", Nil),
    Country("PT", "title.portugal", "PT", Nil),
    Country("US2", "title.puerto_rico", "US2", Nil),
    Country("QA", "title.qatar", "QA", Nil),
    Country("AE4", "title.ras_al_khaimah", "AE4", Nil),
    Country("RE", "title.reunion", "RE", List("Reunion")),
    Country("RO", "title.romania", "RO", Nil),
    Country("AQ4", "title.ross_dependency", "AQ4", Nil),
    Country("RU", "title.russia", "RU", List("USSR", "Soviet Union")),
    Country("RW", "title.rwanda", "RW", Nil),
    Country("BQ1", "title.saba", "BQ1", Nil),
    Country("MY0", "title.sabah", "MY0", Nil),
    Country("WS", "title.samoa", "WS", List("Western Samoa")),
    Country("BL", "title.saint_barthelemy", "BL", List("Barthélemy", "Barthelemy")),
    Country("BQ2", "title.st_eustatius", "BQ2", Nil),
    Country("KN", "title.saint_kitts_and_nevis", "KN", List("St Kitts")),
    Country("SH0", "title.saint_helena_ascension_and_tristan_da_cunha", "SH0", List("St Helena")),
    Country("LC", "title.saint_lucia", "LC", List("St Lucia")),
    Country("GP3", "title.st_maarten_north", "GP3", Nil),
    Country("SX", "title.st_maarten_south", "SX", Nil),
    Country("PM", "title.saint_pierre_and_miquelon", "PM", List("St Pierre")),
    Country("VC", "title.saint_vincent_and_the_grenadines", "VC", List("St Vincent")),
    Country("SM", "title.san_marino", "SM", Nil),
    Country("ST", "title.sao_tome_and_principe", "ST", Nil),
    Country("MY1", "title.sarawak", "MY1", Nil),
    Country("SA", "title.saudi_arabia", "SA", Nil),
    Country("SN", "title.senegal", "SN", Nil),
    Country("XS", "title.serbia", "XS", Nil),
    Country("SC", "title.seychelles", "SC", Nil),
    Country("AE5", "title.sharjah", "AE5", Nil),
    Country("SL", "title.sierra_leone", "SL", Nil),
    Country("SG", "title.singapore", "SG", Nil),
    Country("SK", "title.slovakia", "SK", Nil),
    Country("SI", "title.slovenia", "SI", Nil),
    Country("SB", "title.solomon_islands", "SB", List("Solomons")),
    Country("SO", "title.somalia", "SO", Nil),
    Country("ZA", "title.south_africa", "ZA", List("RSA")),
    Country("GS", "title.south_georgia_and_the_south_sandwich_islands", "GS", Nil),
    Country("ES0", "title.spain", "ES0", Nil),
    Country("LK", "title.sri_lanka", "LK", Nil),
    Country("SD", "title.north_sudan", "SD", Nil),
    Country("SS", "title.south_sudan", "SS", Nil),
    Country("SR", "title.suriname", "SR", Nil),
    Country("NO0", "title.svalbard_archipelago", "NO0", Nil),
    Country("HN0", "title.swan_islands", "HN0", Nil),
    Country("SZ", "title.swaziland", "SZ", Nil),
    Country("SE", "title.sweden", "SE", Nil),
    Country("CH", "title.switzerland", "CH", List("Swiss")),
    Country("SY", "title.syria", "SY", Nil),
    Country("TW", "title.taiwan", "TW", Nil),
    Country("TJ", "title.tajikistan", "TJ", Nil),
    Country("TZ", "title.tanzania", "TZ", Nil),
    Country("TH", "title.thailand", "TH", Nil),
    Country("TG", "title.togo", "TG", List("Togo Republic", "Togolese Republic", "Republic of Togo")),
    Country("TK", "title.tokelau", "TK", Nil),
    Country("TO", "title.tonga", "TO", Nil),
    Country("TT", "title.trinidad_and_tobago", "TT", Nil),
    Country("SH1", "title.tristan_da_cunha", "SH1", Nil),
    Country("TN", "title.tunisia", "TN", Nil),
    Country("TR", "title.turkey", "TR", Nil),
    Country("TM", "title.turkmenistan", "TM", Nil),
    Country("TC", "title.turks_and_caicos_islands", "TC", Nil),
    Country("TV", "title.tuvalu", "TV", Nil),
    Country("UG", "title.uganda", "UG", Nil),
    Country("UA", "title.ukraine", "UA", Nil),
    Country("AE", "title.united_arab_emirates", "AE", List("UAE", "emirati", "dubai", "abu dahbi", "abu dhabi")),
    Country("AE7", "title.umm_al_qaiwain", "AE7", Nil),
    Country("GB", "title.united_kingdom_of_great_britain_and_northern_ireland_the", "GB", List("England", "Scotland", "Wales", "Northern Ireland", "GB", "UK")),
    Country("UM9", "title.united_states_minor_outlying_islands", "UM9", Nil),
    Country("US", "title.united_states_of_america", "US", List("USA", "US", "American")),
    Country("UY", "title.uruguay", "UY", Nil),
    Country("UZ", "title.uzbekistan", "UZ", Nil),
    Country("VU", "title.vanuatu", "VU", Nil),
    Country("VA", "title.vatican_city", "VA", Nil),
    Country("VE", "title.venezuela", "VE", Nil),
    Country("VN", "title.viet_nam", "VN", Nil),
    Country("VI", "title.virgin_islands_us", "VI", Nil),
    Country("UM10", "title.wake_island", "UM10", Nil),
    Country("WF", "title.wallis_and_futuna", "WF", Nil),
    Country("EH", "title.western_sahara", "EH", Nil),
    Country("YE", "title.yemen", "YE", Nil),
    Country("ZM", "title.zambia", "ZM", Nil),
    Country("ZW", "title.zimbabwe", "ZW", Nil)
  )

  private val countriesById = countries.map {
    case country @ Country(code, _, _, _) =>
      code -> country
  }.toMap
}
