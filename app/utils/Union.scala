/*
 * Copyright 2025 HM Revenue & Customs
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

package utils

import play.api.libs.json._

import scala.reflect.ClassTag

class Union[A](
  typeField: String,
  readWith: PartialFunction[String, JsValue => JsResult[A]],
  writeWith: PartialFunction[A, JsObject]
) {

  def andLazy[B <: A](typeTag: String, fmt: => OFormat[B])(implicit ct: ClassTag[B]): Union[A] = {
    val readCase: PartialFunction[String, JsValue => JsResult[A]] = { case `typeTag` =>
      (jsValue: JsValue) => Json.fromJson(jsValue)(fmt).asInstanceOf[JsResult[A]]
    }

    val writeCase: PartialFunction[A, JsObject] = { case value: B =>
      Json.toJsObject(value)(fmt) ++ Json.obj(typeField -> typeTag)
    }

    new Union(typeField, readWith.orElse(readCase), writeWith.orElse(writeCase))
  }

  def and[B <: A](typeTag: String)(implicit ct: ClassTag[B], f: OFormat[B]): Union[A] =
    andLazy(typeTag, f)

  private val defaultReads: PartialFunction[String, JsValue => JsResult[A]] = { case attemptedType =>
    jsValue => JsError(__ \ typeField, s"$attemptedType is not a recognised $typeField")
  }

  def format: OFormat[A] = {
    val reads = Reads[A] { json =>
      (json \ typeField).validate[String].flatMap { typeTag =>
        readWith.orElse(defaultReads)(typeTag)(json)
      }
    }
    val writes = OWrites[A](writeWith)
    OFormat(reads, writes)
  }
}

object Union {
  def from[A](typeField: String) = new Union[A](typeField, PartialFunction.empty, PartialFunction.empty)
}
