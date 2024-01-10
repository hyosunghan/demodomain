package technology

import groovy.json.JsonSlurper

executor(params)

def executor(def params) {
    def json = new JsonSlurper().parseText(params)
    return mergeInit(json.init, json.exists)
}

def mergeInit(def init, def exists) {
    def result = []
    init.each { i ->
        def platformCode = i.platformCode
        def find = exists.find { it.platformCode == platformCode }
        if (find) {
            result << find
        } else {
            result << i
        }
    }
    return result
}
