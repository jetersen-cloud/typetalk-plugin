package org.jenkinsci.plugins.typetalk.webhookaction

import spock.lang.Specification

class WebhookParserSpec extends Specification {

    def parseJson() {
        setup:
        def parser = new WebhookParser()
        def json = """
{
  "topic": {
    "id": 9526,
    "name": "Typetalk Hack Tokyo Dec 2014",
    "suggestion": "Typetalk Hack Tokyo Dec 2014",
    "lastPostedAt": "2014-12-16T11:17:44Z",
    "createdAt": "2014-12-16T08:32:53Z",
    "updatedAt": "2014-12-16T08:32:53Z"
  },
  "post": {
    "id": 754399,
    "topicId": 9526,
    "replyTo": null,
    "message": "@ikikkobot+ build hello-typetalk-plugin",
    "account": {
      "id": 10000,
      "name": "ikikkotest",
      "fullName": "ikikkotest",
      "suggestion": "ikikkotest",
      "imageUrl": "https://typetalk.in/accounts/10/profile_image.png?t=1413099125640",
      "createdAt": "2012-03-07T05:13:52Z",
      "updatedAt": "2014-12-16T09:12:55Z"
    },
    "mention": null,
    "attachments": [],
    "likes": [],
    "talks": [],
    "links": [],
    "createdAt": "2014-12-16T11:17:44Z",
    "updatedAt": "2014-12-16T11:17:44Z"
  }
}
"""

        expect:
        parser.parseJson(json) == "@ikikkobot+ build hello-typetalk-plugin"
    }

}
