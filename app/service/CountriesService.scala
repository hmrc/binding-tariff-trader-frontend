/*
 * Copyright 2023 HM Revenue & Customs
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

class CountriesService {

  def getAllCountries: List[Country] = countries

  def getAllCountriesById: Map[String, Country] = countriesById

  def autoCompleteSynonymCountries(implicit messages: Messages): JsObject =
    countries
      .flatMap(country => country.toNewAutoCompleteJson)
      .foldLeft(JsObject.empty)((countryList, c) => countryList + c)

  private val countries = List(
    Country("AF", "title.afghanistan", "AF", Nil),
    Country("AL", "title.albania", "AL", Nil),
    Country("DZ", "title.algeria", "DZ", Nil),
    Country("AS", "title.american_samoa", "AS", Nil),
    Country("AD", "title.andorra", "AD", Nil),
    Country("AO", "title.angola", "AO", Nil),
    Country("AI", "title.anguilla", "AI", Nil),
    Country("AQ", "title.antarctica", "AQ", List("Antartica")),
    Country("AG", "title.antigua_and_barbuda", "AG", Nil),
    Country("AR", "title.argentina", "AR", Nil),
    Country("AM", "title.armenia", "AM", Nil),
    Country("AW", "title.aruba", "AW", Nil),
    Country("AU", "title.australia", "AU", List("Oz")),
    Country("AT", "title.austria", "AT", Nil),
    Country("AZ", "title.azerbaijan", "AZ", Nil),
    Country("BH", "title.bahrain", "BH", Nil),
    Country("BD", "title.bangladesh", "BD", Nil),
    Country("BB", "title.barbados", "BB", Nil),
    Country("BY", "title.belarus", "BY", Nil),
    Country("BE", "title.belgium", "BE", Nil),
    Country("BZ", "title.belize", "BZ", Nil),
    Country("BJ", "title.benin", "BJ", Nil),
    Country("BM", "title.bermuda", "BM", Nil),
    Country("BT", "title.bhutan", "BT", Nil),
    Country("BO", "title.bolivia", "BO", Nil),
    Country("BA", "title.bosnia_and_herzegovina", "BA", Nil),
    Country("BW", "title.botswana", "BW", Nil),
    Country("BV", "title.bouvet_island", "BV", Nil),
    Country("BR", "title.brazil", "BR", Nil),
    Country("IO", "title.british_indian_ocean_territory", "IO", List("BIOT")),
    Country("VG", "title.british_virgin_islands", "VG", Nil),
    Country("BN", "title.brunei", "BN", Nil),
    Country("BG", "title.bulgaria", "BG", Nil),
    Country("BF", "title.burkina_faso", "BF", Nil),
    Country("BI", "title.burundi", "BI", Nil),
    Country("KH", "title.cambodia", "KH", Nil),
    Country("CM", "title.cameroon", "CM", Nil),
    Country("CA", "title.canada", "CA", Nil),
    Country("CV", "title.cape_verde", "CV", List("Republic of Cabo Verde")),
    Country("KY", "title.cayman_islands", "KY", List("Caymans")),
    Country("CF", "title.central_african_republic", "CF", List("CAR")),
    Country("TD", "title.chad", "TD", Nil),
    Country("CL", "title.chile", "CL", Nil),
    Country("CN", "title.china", "CN", List("People's Republic of China", "PRC", "Peoples Republic of China")),
    Country("CX", "title.christmas_island", "CX", List("Christmas Islands")),
    Country("CC", "title.cocos_islands", "CC", Nil),
    Country("CO", "title.colombia", "CO", List("Columbia")),
    Country("KM", "title.comoros", "KM", Nil),
    Country("CG", "title.congo", "CG", List("Congo-Brazzaville", "Congo Republic")),
    Country("CD", "title.democratic_republic_of_the_congo", "CD", List("DR Congo", "DRC")),
    Country("CK", "title.cook_islands", "CK", Nil),
    Country("CR", "title.costa_rica", "CR", Nil),
    Country("HR", "title.croatia", "HR", Nil),
    Country("CU", "title.cuba", "CU", Nil),
    Country("CW", "title.curacao", "CW", List("Curacao", "Curacoa")),
    Country("CY", "title.cyprus", "CY", Nil),
    Country("CZ", "title.czechia", "CZ", Nil),
    Country("CS", "title.czechoslovakia", "CS", Nil),
    Country("DK", "title.denmark", "DK", List("Danish")),
    Country("DJ", "title.djibouti", "DJ", Nil),
    Country("DM", "title.dominica", "DM", Nil),
    Country("DO", "title.dominican_republic", "DO", Nil),
    Country("TL", "title.e_east_timor", "TL", Nil),
    Country("EC", "title.ecuador", "EC", Nil),
    Country("EG", "title.egypt", "EG", Nil),
    Country("SV", "title.el_salvador", "SV", Nil),
    Country("GQ", "title.equatorial_guinea", "GQ", Nil),
    Country("ER", "title.eritrea", "ER", Nil),
    Country("EE", "title.estonia", "EE", Nil),
    Country("SZ", "title.eswatini", "SZ", Nil),
    Country("ET", "title.ethiopia", "ET", Nil),
    Country("FK", "title.falkland_islands", "FK", List("Falklands")),
    Country("FO", "title.faroe_islands", "FO", List("Faroes")),
    Country("FJ", "title.fiji", "FJ", Nil),
    Country("FI", "title.finland", "FI", Nil),
    Country("FR", "title.france", "FR", Nil),
    Country("GF", "title.french_guiana", "GF", List("French Guayana")),
    Country("PF", "title.french_polynesia", "PF", List("Polynesian Islands")),
    Country("TF", "title.french_southern_territories", "TF", Nil),
    Country("GA", "title.gabon", "GA", Nil),
    Country("GE", "title.georgia", "GE", Nil),
    Country("DE", "title.germany", "DE", List("Deutschland")),
    Country("GH", "title.ghana", "GH", Nil),
    Country("GI", "title.gibraltar", "GI", List("Gibraltar Rock")),
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
    Country("HM", "title.heard_and_mcdonald_islands", "HM", Nil),
    Country("HN", "title.honduras", "HN", Nil),
    Country("HK", "title.hong_kong", "HK", Nil),
    Country("HU", "title.hungary", "HU", Nil),
    Country("IS", "title.iceland", "IS", Nil),
    Country("IN", "title.india", "IN", Nil),
    Country("ID", "title.indonesia", "ID", Nil),
    Country("IR", "title.iran", "IR", Nil),
    Country("IQ", "title.iraq", "IQ", Nil),
    Country("IE", "title.ireland", "IE", List("Republic of Ireland", "Eire")),
    Country("IM", "title.isle_of_man", "IM", Nil),
    Country("IL", "title.israel", "IL", Nil),
    Country("IT", "title.italy", "IT", Nil),
    Country("CI", "title.ivory_coast", "CI", Nil),
    Country("JM", "title.jamaica", "JM", Nil),
    Country("JP", "title.japan", "JP", Nil),
    Country("JE", "title.jersey", "JE", List("Channel Islands")),
    Country("JO", "title.jordan", "JO", Nil),
    Country("KZ", "title.kazakhstan", "KZ", Nil),
    Country("KE", "title.kenya", "KE", Nil),
    Country("KI", "title.kiribati", "KI", Nil),
    Country("KW", "title.kuwait", "KW", Nil),
    Country("KG", "title.kyrgyzstan", "KG", Nil),
    Country("LA", "title.laos", "LA", List("Lao Peoples Democratic Republic")),
    Country("LV", "title.latvia", "LV", Nil),
    Country("LB", "title.lebanon", "LB", Nil),
    Country("LS", "title.lesotho", "LS", Nil),
    Country("LR", "title.liberia", "LR", Nil),
    Country("LY", "title.libya", "LY", Nil),
    Country("LI", "title.liechtenstein", "LI", Nil),
    Country("LT", "title.lithuania", "LT", Nil),
    Country("LU", "title.luxembourg", "LU", List("Luxemburg")),
    Country("MO", "title.macao", "MO", Nil),
    Country("MG", "title.madagascar", "MG", Nil),
    Country("MW", "title.malawi", "MW", Nil),
    Country("MY", "title.malaysia", "MY", Nil),
    Country("MV", "title.maldives", "MV", List("Maldive Islands")),
    Country("ML", "title.mali", "ML", List("Mali Republic", "Republic of Mali")),
    Country("MT", "title.malta", "MT", Nil),
    Country("MH", "title.marshall_islands", "MH", Nil),
    Country("MQ", "title.martinique", "MQ", Nil),
    Country("MR", "title.mauritania", "MR", Nil),
    Country("MU", "title.mauritius", "MU", Nil),
    Country("YT", "title.mayotte", "YT", Nil),
    Country("MX", "title.mexico", "MX", Nil),
    Country("FM", "title.micronesia", "FM", Nil),
    Country("MD", "title.moldova", "MD", Nil),
    Country("MC", "title.monaco", "MC", Nil),
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
    Country("KP", "title.north_korea", "KP", Nil),
    Country("MK", "title.north_macedonia", "MK", Nil),
    Country("MP", "title.northern_mariana_islands", "MP", Nil),
    Country("NO", "title.norway", "NO", Nil),
    Country("PS", "title.occupied_palestinian_territories", "PS", Nil),
    Country("OM", "title.oman", "OM", Nil),
    Country("PK", "title.pakistan", "PK", Nil),
    Country("PW", "title.palau", "PW", Nil),
    Country("PA", "title.panama", "PA", Nil),
    Country("PG", "title.papua_new_guinea", "PG", Nil),
    Country("PY", "title.paraguay", "PY", Nil),
    Country("PE", "title.peru", "PE", Nil),
    Country("PH", "title.philippines", "PH", List("Philippenes", "Phillipines", "Phillippines", "Philipines")),
    Country("PN", "title.pitcairn", "PN", Nil),
    Country("PL", "title.poland", "PL", Nil),
    Country("PT", "title.portugal", "PT", Nil),
    Country("PR", "title.puerto_rico", "PR", Nil),
    Country("QA", "title.qatar", "QA", Nil),
    Country("RO", "title.romania", "RO", Nil),
    Country("RU", "title.russia", "RU", List("USSR", "Soviet Union")),
    Country("RW", "title.rwanda", "RW", Nil),
    Country("RE", "title.reunion", "RE", List("Reunion")),
    Country("PM", "title.saint_pierre_and_miquelon", "PM", List("St Pierre")),
    Country("MF", "title.saint_martin_french_part", "MF", Nil),
    Country("WS", "title.samoa", "WS", List("Western Samoa")),
    Country("SM", "title.san_marino", "SM", Nil),
    Country("ST", "title.sao_tome_and_principe", "ST", Nil),
    Country("SA", "title.saudi_arabia", "SA", Nil),
    Country("SN", "title.senegal", "SN", Nil),
    Country("RS", "title.serbia", "RS", Nil),
    Country("SC", "title.seychelles", "SC", Nil),
    Country("SL", "title.sierra_leone", "SL", Nil),
    Country("SG", "title.singapore", "SG", Nil),
    Country("SX", "title.sint_maarten_dutch_part", "SX", Nil),
    Country("SK", "title.slovakia", "SK", Nil),
    Country("SI", "title.slovenia", "SI", Nil),
    Country("SB", "title.solomon_islands", "SB", List("Solomons")),
    Country("SO", "title.somalia", "SO", Nil),
    Country("ZA", "title.south_africa", "ZA", List("RSA")),
    Country("GS", "title.south_georgia_and_the_south_sandwich_islands", "GS", Nil),
    Country("KR", "title.south_korea", "KR", Nil),
    Country("SS", "title.south_sudan", "SS", Nil),
    Country("ES", "title.spain", "ES", Nil),
    Country("LK", "title.sri_lanka", "LK", Nil),
    Country("KN", "title.st_kitts_and_nevis", "KN", Nil),
    Country("LC", "title.saint_lucia", "LC", List("St Lucia")),
    Country("VC", "title.saint_vincent", "VC", List("St Vincent")),
    Country("SD", "title.sudan", "SD", Nil),
    Country("SR", "title.suriname", "SR", Nil),
    Country("SJ", "title.svalbard_and_jan_mayen", "SJ", Nil),
    Country("SE", "title.sweden", "SE", Nil),
    Country("CH", "title.switzerland", "CH", List("Swiss")),
    Country("SY", "title.syria", "SY", Nil),
    Country("TW", "title.taiwan", "TW", Nil),
    Country("TJ", "title.tajikistan", "TJ", Nil),
    Country("TZ", "title.tanzania", "TZ", Nil),
    Country("TH", "title.thailand", "TH", Nil),
    Country("BS", "title.the_bahamas", "BS", Nil),
    Country("GM", "title.the_gambia", "GM", Nil),
    Country("TG", "title.togo", "TG", List("Togo Republic", "Togolese Republic", "Republic of Togo")),
    Country("TK", "title.tokelau", "TK", Nil),
    Country("TO", "title.tonga", "TO", Nil),
    Country("TT", "title.trinidad_and_tobago", "TT", Nil),
    Country("TN", "title.tunisia", "TN", Nil),
    Country("TR", "title.turkey", "TR", Nil),
    Country("TM", "title.turkmenistan", "TM", Nil),
    Country("TC", "title.turks_and_caicos_islands", "TC", Nil),
    Country("TV", "title.tuvalu", "TV", Nil),
    Country("UG", "title.uganda", "UG", Nil),
    Country("UA", "title.ukraine", "UA", Nil),
    Country("AE", "title.united_arab_emirates", "AE", List("UAE", "emirati", "dubai", "abu dahbi", "abu dhabi")),
    Country("GB", "title.united_kingdom", "GB", List("England", "Scotland", "Wales", "Northern Ireland", "GB", "UK")),
    Country("US", "title.united_states", "US", List("USA", "US", "American")),
    Country("VI", "title.united_states_virgin_islands", "VI", Nil),
    Country("UY", "title.uruguay", "UY", Nil),
    Country("UZ", "title.uzbekistan", "UZ", Nil),
    Country("VU", "title.vanuatu", "VU", Nil),
    Country("VA", "title.vatican_city", "VA", Nil),
    Country("VE", "title.venezuela", "VE", Nil),
    Country("VN", "title.viet_nam", "VN", Nil),
    Country("WF", "title.wallis_and_futuna", "WF", Nil),
    Country("EH", "title.western_sahara", "EH", Nil),
    Country("YE", "title.yemen", "YE", Nil),
    Country("ZM", "title.zambia", "ZM", Nil),
    Country("ZW", "title.zimbabwe", "ZW", Nil),
    Country("AX", "title.aland_islands", "AX", List("Aland Islands"))
  )

  private val countriesById = countries.map {
    case country @ Country(code, _, _, _) =>
      code -> country
  }.toMap
}
