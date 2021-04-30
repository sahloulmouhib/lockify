package eniso.ia.lockify

class Persondata class Person(
    var firstName: String="",
    var lastName:String="",
    var email: String ="",
    var familyMembersMails: MutableList<String> = mutableListOf(),
    var familyMember: Boolean =false,
    var admin :Boolean = false

) {
}
