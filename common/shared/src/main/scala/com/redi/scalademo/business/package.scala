package com.redi.scalademo

package object business {

  type ValidatedResult[+T] = Either[Iterable[ValidationFailure], T]

}
