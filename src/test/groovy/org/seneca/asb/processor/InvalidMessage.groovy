package org.seneca.asb.processor

class InvalidMessage {
    def id = UUID.randomUUID().toString()
    def body = """{"id"="#$id"}""" as String

    @Override
    String toString() {
        return "InvalidMessage{id=" + id
    }
}
