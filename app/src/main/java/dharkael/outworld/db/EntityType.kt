package dharkael.outworld.db

sealed  class EntityType {
    class Calendar: EntityType()
    class CalendarDate: EntityType()
    class Stop: EntityType()
    class Trip: EntityType()
}

