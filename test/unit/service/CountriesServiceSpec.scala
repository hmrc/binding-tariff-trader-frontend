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
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class CountriesServiceSpec extends UnitSpec with MockitoSugar with GuiceOneAppPerSuite {

  val expectedCountriesList = List(
    Country("AF", "title.afghanistan", "AF", isEu=false, Nil),
    Country("AX", "title.aland_islands", "AX", isEu=false, List("Aland Islands")),
    Country("AL", "title.albania", "AL", isEu=false, Nil),
    Country("DZ", "title.algeria", "DZ", isEu=false, Nil),
    Country("AS", "title.american_samoa", "AS", isEu=false, Nil),
    Country("AD", "title.andorra", "AD", isEu=false, Nil),
    Country("AO", "title.angola", "AO", isEu=false, Nil),
    Country("AI", "title.anguilla", "AI", isEu=false, Nil),
    Country("AQ", "title.antarctica", "AQ", isEu=false, List("Antartica")),
    Country("AG", "title.antigua_and_barbuda", "AG", isEu=false, Nil),
    Country("AR", "title.argentina", "AR", isEu=false, Nil),
    Country("AM", "title.armenia", "AM", isEu=false, Nil),
    Country("AW", "title.aruba", "AW", isEu=false, Nil),
    Country("AU", "title.australia", "AU", isEu=false, List("Oz")),
    Country("AT", "title.austria", "AT", isEu=true, Nil),
    Country("AZ", "title.azerbaijan", "AZ", isEu=false, Nil),
    Country("BS", "title.bahamas", "BS", isEu=false, Nil),
    Country("BH", "title.bahrain", "BH", isEu=false, Nil),
    Country("BD", "title.bangladesh", "BD", isEu=false, Nil),
    Country("BB", "title.barbados", "BB", isEu=false, Nil),
    Country("BY", "title.belarus", "BY", isEu=false, Nil),
    Country("BE", "title.belgium", "BE", isEu=true, Nil),
    Country("BZ", "title.belize", "BZ", isEu=false, Nil),
    Country("BJ", "title.benin", "BJ", isEu=false, Nil),
    Country("BM", "title.bermuda", "BM", isEu=false, Nil),
    Country("BT", "title.bhutan", "BT", isEu=false, Nil),
    Country("BO", "title.bolivia", "BO", isEu=false, Nil),
    Country("BQ", "title.bonaire_sint_eustatius_and_saba", "BQ", isEu=false, Nil),
    Country("BA", "title.bosnia_and_herzegovina", "BA", isEu=false, Nil),
    Country("BW", "title.botswana", "BW", isEu=false, Nil),
    Country("BV", "title.bouvet_island", "BV", isEu=false, Nil),
    Country("BR", "title.brazil", "BR", isEu=false, Nil),
    Country("IO", "title.british_indian_ocean_territory", "IO", isEu=false, List("BIOT")),
    Country("BN", "title.brunei_darussalam", "BN", isEu=false, Nil),
    Country("BG", "title.bulgaria", "BG", isEu=true, Nil),
    Country("BF", "title.burkina_faso", "BF", isEu=false, Nil),
    Country("BI", "title.burundi", "BI", isEu=false, Nil),
    Country("CV", "title.cape_verde", "CV", isEu=false, List("Republic of Cabo Verde")),
    Country("KH", "title.cambodia", "KH", isEu=false, Nil),
    Country("CM", "title.cameroon", "CM", isEu=false, Nil),
    Country("CA", "title.canada", "CA", isEu=false, Nil),
    Country("ES2", "title.canary_islands", "ES", isEu=false, List("Canaries")),
    Country("KY", "title.cayman_islands", "KY", isEu=false, List("Caymans")),
    Country("CF", "title.central_african_republic", "CF", isEu=false, List("CAR")),
    Country("TD", "title.chad", "TD", isEu=false, Nil),
    Country("CL", "title.chile", "CL", isEu=false, Nil),
    Country("CN", "title.china", "CN", isEu=false, List("People's Republic of China", "PRC", "Peoples Republic of China")),
    Country("CX", "title.christmas_island", "CX", isEu=false, List("Christmas Islands")),
    Country("CC", "title.cocos_keeling_islands", "CC", isEu=false, Nil),
    Country("CO", "title.colombia", "CO", isEu=false, List("Columbia")),
    Country("KM", "title.comoros", "KM", isEu=false, Nil),
    Country("CD", "title.democratic_republic_of_the_congo", "CD", isEu=false, List("DR Congo", "DRC")),
    Country("CG", "title.republic_of_the_congo", "CG", isEu=false, List("Congo-Brazzaville", "Congo Republic")),
    Country("CK", "title.cook_islands", "CK", isEu=false, Nil),
    Country("CR", "title.costa_rica", "CR", isEu=false, Nil),
    Country("CI", "title.cote_divoire", "CI", isEu=false, List("Ivory Coast", "Cote d'Ivoire", "Cote dIvoire", "Cote d Ivoire")),
    Country("HR", "title.croatia", "HR", isEu=true, Nil),
    Country("CU", "title.cuba", "CU", isEu=false, Nil),
    Country("CW", "title.curacao", "CW", isEu=false, List("Curacao", "Curacoa")),
    Country("CY0", "title.cyprus", "CY", isEu=true, Nil),
    Country("CZ", "title.czech_republic", "CZ", isEu=true, List("Czechoslovakia", "Czechia")),
    Country("DK", "title.denmark", "DK", isEu=true, List("Danish")),
    Country("DJ", "title.djibouti", "DJ", isEu=false, Nil),
    Country("DM", "title.dominica", "DM", isEu=false, Nil),
    Country("DO", "title.dominican_republic", "DO", isEu=false, Nil),
    Country("EC", "title.ecuador", "EC", isEu=false, Nil),
    Country("EG", "title.egypt", "EG", isEu=false, Nil),
    Country("SV", "title.el_salvador", "SV", isEu=false, Nil),
    Country("GQ", "title.equatorial_guinea", "GQ", isEu=false, Nil),
    Country("ER", "title.eritrea", "ER", isEu=false, Nil),
    Country("EE", "title.estonia", "EE", isEu=true, Nil),
    Country("ET", "title.ethiopia", "ET", isEu=false, Nil),
    Country("SZ", "title.eswatini", "SZ", isEu=false, List("Swaziland")),
    Country("FK", "title.falkland_islands", "FK", isEu=false, List("Falklands")),
    Country("FO", "title.faroe_islands", "FO", isEu=false, List("Faroes")),
    Country("FJ", "title.fiji", "FJ", isEu=false, Nil),
    Country("FI", "title.finland", "FI", isEu=true, Nil),
    Country("FR", "title.france", "FR", isEu=true, Nil),
    Country("GF", "title.french_guiana", "GF", isEu=false, List("French Guayana")),
    Country("PF", "title.french_polynesia", "PF", isEu=false, List("Polynesian Islands")),
    Country("TF", "title.french_southern_territories", "TF", isEu=false, Nil),
    Country("ES3", "title.fuerteventura", "ES", isEu = false, Nil),
    Country("GA", "title.gabon", "GA", isEu=false, Nil),
    Country("GM", "title.gambia", "GM", isEu=false, Nil),
    Country("GE", "title.georgia", "GE", isEu=false, Nil),
    Country("DE", "title.germany", "DE", isEu=true, List("Deutschland")),
    Country("GH", "title.ghana", "GH", isEu=false, Nil),
    Country("GI", "title.gibraltar", "GI", isEu=false, List("Gibraltar Rock")),
    Country("ES4", "title.gran_canaria", "ES", isEu=false, List("Grand Canaria")),
    Country("GR", "title.greece", "GR", isEu=true, Nil),
    Country("GL", "title.greenland", "GL", isEu=false, Nil),
    Country("GD", "title.grenada", "GD", isEu=false, Nil),
    Country("GP", "title.guadeloupe", "GP", isEu=false, Nil),
    Country("GU", "title.guam", "GU", isEu=false, Nil),
    Country("GT", "title.guatemala", "GT", isEu=false, Nil),
    Country("GG", "title.guernsey", "GG", isEu=false, List("Channel Islands")),
    Country("GN", "title.guinea", "GN", isEu=false, Nil),
    Country("GW", "title.guinea_bissau", "GW", isEu=false, Nil),
    Country("GY", "title.guyana", "GY", isEu=false, Nil),
    Country("HT", "title.haiti", "HT", isEu=false, Nil),
    Country("HM", "title.heard_island_and_mcdonald_islands", "HM", isEu=false, Nil),
    Country("VA", "title.holy_see", "VA", isEu=false, Nil),
    Country("HN", "title.honduras", "HN", isEu=false, Nil),
    Country("HK", "title.hong_kong", "HK", isEu=false, Nil),
    Country("HU", "title.hungary", "HU", isEu=true, Nil),
    Country("IS", "title.iceland", "IS", isEu=false, Nil),
    Country("IN", "title.india", "IN", isEu=false, Nil),
    Country("ID", "title.indonesia", "ID", isEu=false, Nil),
    Country("IR", "title.iran", "IR", isEu=false, Nil),
    Country("IQ", "title.iraq", "IQ", isEu=false, Nil),
    Country("IE", "title.ireland", "IE", isEu=true, List("Republic of Ireland", "Eire")),
    Country("IM", "title.isle_of_man", "IM", isEu=false, Nil),
    Country("IL", "title.israel", "IL", isEu=false, Nil),
    Country("IT", "title.italy", "IT", isEu=true, Nil),
    Country("JM", "title.jamaica", "JM", isEu=false, Nil),
    Country("JP", "title.japan", "JP", isEu=false, Nil),
    Country("JE", "title.jersey", "JE", isEu=false, List("Channel Islands")),
    Country("JO", "title.jordan", "JO", isEu=false, Nil),
    Country("KZ", "title.kazakhstan", "KZ", isEu=false, Nil),
    Country("KE", "title.kenya", "KE", isEu=false, Nil),
    Country("KI", "title.kiribati", "KI", isEu=false, Nil),
    Country("KP", "title.north_korea", "KP", isEu=false, Nil),
    Country("KR", "title.south_korea", "KR", isEu=false, Nil),
    Country("KW", "title.kuwait", "KW", isEu=false, Nil),
    Country("KG", "title.kyrgyzstan", "KG", isEu=false, Nil),
    Country("ES5", "title.lanzarote", "ES", isEu=false, List("Lanzarrote")),
    Country("ES6", "title.la_palma", "ES", isEu=false, List("Las Palma")),
    Country("LA", "title.lao_peoples_democratic_republic", "LA", isEu=false, List("Lao Peoples Democratic Republic")),
    Country("LV", "title.latvia", "LV", isEu=true, Nil),
    Country("LB", "title.lebanon", "LB", isEu=false, Nil),
    Country("LS", "title.lesotho", "LS", isEu=false, Nil),
    Country("LR", "title.liberia", "LR", isEu=false, Nil),
    Country("LY", "title.libya", "LY", isEu=false, Nil),
    Country("LI", "title.liechtenstein", "LI", isEu=false, Nil),
    Country("LT", "title.lithuania", "LT", isEu=true, Nil),
    Country("LU", "title.luxembourg", "LU", isEu=true, List("Luxemburg")),
    Country("MO", "title.macao", "MO", isEu=false, Nil),
    Country("MK", "title.macedonia", "MK", isEu=false, Nil),
    Country("MG", "title.madagascar", "MG", isEu=false, Nil),
    Country("MW", "title.malawi", "MW", isEu=false, Nil),
    Country("MY", "title.malaysia", "MY", isEu=false, Nil),
    Country("MV", "title.maldives", "MV", isEu=false, List("Maldive Islands")),
    Country("ML", "title.mali", "ML", isEu=false, List("Mali Republic", "Republic of Mali")),
    Country("MT", "title.malta", "MT", isEu=true, Nil),
    Country("MH", "title.marshall_islands", "MH", isEu=false, Nil),
    Country("MQ", "title.martinique", "MQ", isEu=false, Nil),
    Country("MR", "title.mauritania", "MR", isEu=false, Nil),
    Country("MU", "title.mauritius", "MU", isEu=false, Nil),
    Country("YT", "title.mayotte", "YT", isEu=false, Nil),
    Country("MX", "title.mexico", "MX", isEu=false, Nil),
    Country("FM", "title.micronesia", "FM", isEu=false, Nil),
    Country("MD", "title.moldova", "MD", isEu=false, Nil),
    Country("MC", "title.monaco", "MC", isEu=false, Nil),
    Country("MN", "title.mongolia", "MN", isEu=false, Nil),
    Country("ME", "title.montenegro", "ME", isEu=false, Nil),
    Country("MS", "title.montserrat", "MS", isEu=false, Nil),
    Country("MA", "title.morocco", "MA", isEu=false, Nil),
    Country("MZ", "title.mozambique", "MZ", isEu=false, Nil),
    Country("MM", "title.myanmar", "MM", isEu=false, Nil),
    Country("NA", "title.namibia", "NA", isEu=false, Nil),
    Country("NR", "title.nauru", "NR", isEu=false, Nil),
    Country("NP", "title.nepal", "NP", isEu=false, Nil),
    Country("NL", "title.netherlands", "NL", isEu=true, List("Holland", "Dutch", "Amsterdam")),
    Country("NC", "title.new_caledonia", "NC", isEu=false, Nil),
    Country("NZ", "title.new_zealand", "NZ", isEu=false, Nil),
    Country("NI", "title.nicaragua", "NI", isEu=false, Nil),
    Country("NE", "title.niger", "NE", isEu=false, List("Republic of the Niger", "Niger Republic")),
    Country("NG", "title.nigeria", "NG", isEu=false, Nil),
    Country("NU", "title.niue", "NU", isEu=false, Nil),
    Country("NF", "title.norfolk_island", "NF", isEu=false, Nil),
    Country("MP", "title.northern_mariana_islands", "MP", isEu=false, Nil),
    Country("NO", "title.norway", "NO", isEu=false, Nil),
    Country("OM", "title.oman", "OM", isEu=false, Nil),
    Country("PK", "title.pakistan", "PK", isEu=false, Nil),
    Country("PW", "title.palau", "PW", isEu=false, Nil),
    Country("PS", "title.palestine_state_of", "PS", isEu=false, Nil),
    Country("PA", "title.panama", "PA", isEu=false, Nil),
    Country("PG", "title.papua_new_guinea", "PG", isEu=false, Nil),
    Country("PY", "title.paraguay", "PY", isEu=false, Nil),
    Country("PE", "title.peru", "PE", isEu=false, Nil),
    Country("PH", "title.philippines", "PH", isEu=false, List("Philippenes", "Phillipines", "Phillippines", "Philipines")),
    Country("PN", "title.pitcairn", "PN", isEu=false, Nil),
    Country("PL", "title.poland", "PL", isEu=true, Nil),
    Country("PT", "title.portugal", "PT", isEu=true, Nil),
    Country("PR", "title.puerto_rico", "PR", isEu=false, Nil),
    Country("QA", "title.qatar", "QA", isEu=false, Nil),
    Country("RE", "title.reunion", "RE", isEu=false, List("Reunion")),
    Country("RO", "title.romania", "RO", isEu=true, Nil),
    Country("RU", "title.russian_federation", "RU", isEu=false, List("USSR", "Soviet Union")),
    Country("RW", "title.rwanda", "RW", isEu=false, Nil),
    Country("BL", "title.saint_barthelemy", "BL", isEu=false, List("Barthélemy", "Barthelemy")),
    Country("SH", "title.saint_helena_ascension_and_tristan_da_cunha", "SH", isEu=false, List("St Helena")),
    Country("KN", "title.saint_kitts_and_nevis", "KN", isEu=false, List("St Kitts")),
    Country("LC", "title.saint_lucia", "LC", isEu=false, List("St Lucia")),
    Country("MF", "title.saint_martin_french_part", "MF", isEu=false, List("St Martin")),
    Country("PM", "title.saint_pierre_and_miquelon", "PM", isEu=false, List("St Pierre")),
    Country("VC", "title.saint_vincent_and_the_grenadines", "VC", isEu=false, List("St Vincent")),
    Country("WS", "title.samoa", "WS", isEu=false, List("Western Samoa")),
    Country("SM", "title.san_marino", "SM", isEu=false, Nil),
    Country("ST", "title.sao_tome_and_principe", "ST", isEu=false, Nil),
    Country("SA", "title.saudi_arabia", "SA", isEu=false, Nil),
    Country("SN", "title.senegal", "SN", isEu=false, Nil),
    Country("RS", "title.serbia", "RS", isEu=false, Nil),
    Country("SC", "title.seychelles", "SC", isEu=false, Nil),
    Country("SL", "title.sierra_leone", "SL", isEu=false, Nil),
    Country("SG", "title.singapore", "SG", isEu=false, Nil),
    Country("SX", "title.sint_maarten_dutch_part", "SX", isEu=false, Nil),
    Country("SK", "title.slovakia", "SK", isEu=true, Nil),
    Country("SI", "title.slovenia", "SI", isEu=true, Nil),
    Country("SB", "title.solomon_islands", "SB", isEu=false, List("Solomons")),
    Country("SO", "title.somalia", "SO", isEu=false, Nil),
    Country("ZA", "title.republic_of_south_africa", "ZA", isEu=false, List("RSA")),
    Country("GS", "title.south_georgia_and_the_south_sandwich_islands", "GS", isEu=false, Nil),
    Country("SS", "title.south_sudan", "SS", isEu=false, Nil),
    Country("ES0", "title.spain", "ES", isEu=true, Nil),
    Country("LK", "title.sri_lanka", "LK", isEu=false, Nil),
    Country("SD", "title.sudan", "SD", isEu=false, Nil),
    Country("SR", "title.suriname", "SR", isEu=false, Nil),
    Country("SJ", "title.svalbard_and_jan_mayen", "SJ", isEu=false, Nil),
    Country("SE", "title.sweden", "SE", isEu=true, Nil),
    Country("CH", "title.switzerland", "CH", isEu=false, List("Swiss")),
    Country("SY", "title.syrian_arab_republic", "SY", isEu=false, Nil),
    Country("TW", "title.taiwan_province_of_china", "TW", isEu=false, Nil),
    Country("TJ", "title.tajikistan", "TJ", isEu=false, Nil),
    Country("TZ", "title.tanzania", "TZ", isEu=false, Nil),
    Country("ES1", "title.tenerife", "ES", isEu=false, List("Tennerife")),
    Country("TH", "title.thailand", "TH", isEu=false, Nil),
    Country("TL", "title.timor_leste", "TL", isEu=false, Nil),
    Country("TG", "title.togo", "TG", isEu=false, List("Togo Republic", "Togolese Republic", "Republic of Togo")),
    Country("TK", "title.tokelau", "TK", isEu=false, Nil),
    Country("TO", "title.tonga", "TO", isEu=false, Nil),
    Country("TT", "title.trinidad_and_tobago", "TT", isEu=false, Nil),
    Country("TN", "title.tunisia", "TN", isEu=false, Nil),
    Country("TR", "title.turkey", "TR", isEu=false, Nil),
    Country("TM", "title.turkmenistan", "TM", isEu=false, Nil),
    Country("TC", "title.turks_and_caicos_islands", "TC", isEu=false, Nil),
    Country("TV", "title.tuvalu", "TV", isEu=false, Nil),
    Country("UG", "title.uganda", "UG", isEu=false, Nil),
    Country("UA", "title.ukraine", "UA", isEu=false, Nil),
    Country("AE", "title.united_arab_emirates", "AE", isEu=false, List("UAE", "emirati", "dubai", "abu dahbi", "abu dhabi")),
    Country("GB", "title.united_kingdom_of_great_britain_and_northern_ireland_the", "GB", isEu=true, List("England", "Scotland", "Wales", "Northern Ireland", "GB", "UK")),
    Country("UM", "title.united_states_minor_outlying_islands", "UM", isEu=false, Nil),
    Country("US", "title.united_states_of_america", "US", isEu=false, List("USA", "US", "American")),
    Country("UY", "title.uruguay", "UY", isEu=false, Nil),
    Country("UZ", "title.uzbekistan", "UZ", isEu=false, Nil),
    Country("VU", "title.vanuatu", "VU", isEu=false, Nil),
    Country("VE", "title.venezuela", "VE", isEu=false, Nil),
    Country("VN", "title.viet_nam", "VN", isEu=false, Nil),
    Country("VG", "title.virgin_islands_british", "VG", isEu=false, Nil),
    Country("VI", "title.virgin_islands_us", "VI", isEu=false, Nil),
    Country("WF", "title.wallis_and_futuna", "WF", isEu=false, Nil),
    Country("EH", "title.western_sahara", "EH", isEu=false, Nil),
    Country("YE", "title.yemen", "YE", isEu=false, Nil),
    Country("ZM", "title.zambia", "ZM", isEu=false, Nil),
    Country("ZW", "title.zimbabwe", "ZW", isEu=false, Nil)
  )

  "getAllCountries" should {

    val countriesService = app.injector.instanceOf[CountriesService] //CountriesService.getAllCountries

    "return the expected countries" in {
      countriesService.getAllCountries shouldEqual expectedCountriesList
    }

    "not return 2 countries with the same code" in {

      val grouped = countriesService.getAllCountries.groupBy(_.code)
      val duplicates = for (group <- grouped if group._2.size > 1) yield {
        group
      }

      duplicates shouldBe empty
    }
  }

  "isInEu" should {

    val countriesService = app.injector.instanceOf[CountriesService]

    "return true for countries in EU" in {
      countriesService.isInEu("ES0") shouldBe true
    }

    "return false for countries not in EU" in {
      countriesService.isInEu("TN") shouldBe false
    }
  }

  "getCountryByCode" should {

    val countriesService = app.injector.instanceOf[CountriesService]

    "get a country by its code" in {
      countriesService.getCountryByCode("ES0") shouldBe Some(Country("ES0", "title.spain", "ES", isEu = true, Nil))
    }
  }
}
