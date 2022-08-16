package com.mr3y.poodle.network

import com.mr3y.poodle.network.models.JitPackArtifact
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.MavenCentralArtifact
import com.mr3y.poodle.network.models.MavenCentralResponse
import com.mr3y.poodle.network.models.MavenCentralResponseHeader
import com.mr3y.poodle.network.models.ResponseBody

internal val fakeMavenCentralSerializedResponse = """
{
  "responseHeader": {

  },
  "response": {
    "numFound": 2,
    "start": 0,
    "docs": [
      {
        "id": "io.github.a914-gowtham:compose-ratingbar",
        "g": "io.github.a914-gowtham",
        "a": "compose-ratingbar",
        "latestVersion": "1.2.3",
        "repositoryId": "central",
        "p": "aar",
        "timestamp": 1644000428000,
        "versionCount": 13,
        "text": [
          "io.github.a914-gowtham",
          "compose-ratingbar",
          "-javadoc.jar",
          "-sources.jar",
          ".aar",
          ".module",
          ".pom"
        ],
        "ec": [
          "-javadoc.jar",
          "-sources.jar",
          ".aar",
          ".module",
          ".pom"
        ]
      },
      {
        "id": "de.charlex.compose:speeddial",
        "g": "de.charlex.compose",
        "a": "speeddial",
        "latestVersion": "1.0.0-beta03",
        "repositoryId": "central",
        "p": "aar",
        "timestamp": 1623868446000,
        "versionCount": 7,
        "text": [
          "de.charlex.compose",
          "speeddial",
          ".pom.asc.sha256",
          "-sources.jar",
          ".aar",
          ".aar.asc.sha512",
          "-sources.jar.sha512",
          ".aar.sha512",
          ".pom.asc.sha512",
          ".pom.sha256",
          "-sources.jar.sha256",
          ".aar.asc.sha256",
          "-sources.jar.asc.sha512",
          ".aar.sha256",
          ".pom.sha512",
          ".pom",
          "-sources.jar.asc.sha256"
        ],
        "ec": [
          ".pom.asc.sha256",
          "-sources.jar",
          ".aar",
          ".aar.asc.sha512",
          "-sources.jar.sha512",
          ".aar.sha512",
          ".pom.asc.sha512",
          ".pom.sha256",
          "-sources.jar.sha256",
          ".aar.asc.sha256",
          "-sources.jar.asc.sha512",
          ".aar.sha256",
          ".pom.sha512",
          ".pom",
          "-sources.jar.asc.sha256"
        ]
      }
    ]
  }
}
""".trimIndent()

internal val invalidMavenCentralSerializedResponse = """
    {
      "numFound": 2,
      "start": 0,
      "docs": [
        {
          "id": "io.github.a914-gowtham:compose-ratingbar",
          "g": "io.github.a914-gowtham",
          "a": "compose-ratingbar",
          "latestVersion": "1.2.3",
          "repositoryId": "central",
          "p": "aar",
          "timestamp": 1644000428000,
          "versionCount": 13,
          "text": [
            "io.github.a914-gowtham",
            "compose-ratingbar",
            "-javadoc.jar",
            "-sources.jar",
            ".aar",
            ".module",
            ".pom"
          ],
          "ec": [
            "-javadoc.jar",
            "-sources.jar",
            ".aar",
            ".module",
            ".pom"
          ]
        },
    }
""".trimIndent()

internal val fakeJitPackSerializedResponse = """
    {
        "com.github.hitgif:textfieldboxes": [],
        "com.github.androiddeveloperlb:autofittextview": [],
        "com.github.yvescheung:rollingtext": [],
        "com.github.iambedant:outlinetextview": [],
        "com.github.carterhudson:html-textview": [],
        "com.github.mzcretin:expandabletextview": [],
        "com.github.chenbingx:supertextview": [],
        "com.github.mahimrocky:showmoretext": [],
        "com.andreabaccega:android-form-edittext": [],
        "com.github.mabbas007:tagsedittext": [],
        "com.github.xiaweizi:marqueetextview": [],
        "com.github.swapnil1104:otpedittext": [],
        "com.github.deano2390:flowtextview": [],
        "com.github.amulyakhare:textdrawable": [],
        "com.github.wednesday-solutions:creditcardedittext": [],
        "com.github.egslava:edittext-mask": []
    }
""".trimIndent()

internal val fakeJitPackResponseMetadata = buildList { repeat(16) { add(produceFakeMetadata()) } }

internal val invalidJitPackSerializedResponse = """
    {
        "com.github.contentful:rich-text-renderer-java": [],
        "com.github.texttechnologylab:textimager-uima": [],
        "com.github.santalu:mask-edittext": [],
        "com.github.poovamraj:pinedittextfield": [],
        "com.github.ramseth001:textdrawable": [],
        "com.github.cielsk:clearable-edittext": [],
        "com.github.blackcat27:currencyedittext": [],
        "com.github.lygttpod:supertextview": [],
        "com.github.viksaaskool:autofitedittext": [],
        "com.github.scottyab:showhidepasswordedittext": [],
        "com.github.hitgif:textfieldboxes": [],
        "com.github.androiddeveloperlb:autofittextview": [],
        "com.github.yvescheung:rollingtext": [],
        "com.github.iambedant:outlinetextview": [],
        "com.github.carterhudson:html-textview": [],
        "com.github.mzcretin:expandabletextview": [],
        "com.github.chenbingx:supertextview": [],
        "com.github.mahimrocky:showmoretext": [],
        "com.andreabaccega:android-form-edittext": [],
        "com.github.mabbas007:tagsedittext": [],
        "com.github.xiaweizi:marqueetextview": [],
        "com.github.swapnil1104:otpedittext": [],
        "com.github.deano2390:flowtextview": [],
        "com.github.amulyakhare:textdrawable": [],
        "com.github.wednesday-solutions:creditcardedittext": [],
        "com.github.egslava:edittext-mask": [],
        "com.github.victorminerva:autoresizeedittext": [],
        "com.github.wordpress-mobile:react-native-safe-area-context": [],
        "com.github.dimorinny:floating-text-button": [],
        "com.github.tobiasschuerg:android-prefix-suffix-edit-text": [],
        "com.github.kyash:validatable-textinput-layout": [],
        "com.github.ammargitham:autolinktextviewv2": [],
        "com.github.btranslations:text-processing-utils": [],
        "io.github.hakky54:sslcontext-kickstart": [],
        "com.github.johnkil:android-robototextview": [],
        "com.github.wizenoze:justext-java": [],
        "com.github.mukeshsolanki:google-places-autocomplete-edittext": [],
        "com.github.vilyever:androidcontextholder": [],
        "me.austinhuang:autolinktextviewv2": [],
        "com.github.apg-mobile:android-round-textview": [],
        "com.github.gcx-hci:grandcentrix-formatted-text-android": [],
        "com.github.paradoxie:autoverticaltextview": [],
        "com.github.limedroid:xrichtext": [],
        "com.github.jmperezra:highlighttextview": [],
        "com.github.ilgun:expandingtextarea": [],
        "com.github.sendtion:xrichtext": [],
        "com.github.tylersuehr7:social-text-view": [],
        "com.github.elevenetc:textsurface": [],
        "com.github.blackboxvision:datetimepicker-edittext": [],
        "com.github.yalantis:context-menu.android": []
    
""".trimIndent()

internal val fakeJitPackDeserializedResponse = JitPackResponse(
    listOf(
        JitPackArtifact("com.github.hitgif:textfieldboxes", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.androiddeveloperlb:autofittextview", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.yvescheung:rollingtext", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.iambedant:outlinetextview", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.carterhudson:html-textview", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.mzcretin:expandabletextview", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.chenbingx:supertextview", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.mahimrocky:showmoretext", "1.0.1", 1607963039390),
        JitPackArtifact("com.andreabaccega:android-form-edittext", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.mabbas007:tagsedittext", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.xiaweizi:marqueetextview", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.swapnil1104:otpedittext", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.deano2390:flowtextview", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.amulyakhare:textdrawable", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.wednesday-solutions:creditcardedittext", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.egslava:edittext-mask", "1.0.1", 1607963039390),
    )
)

internal fun produceFakeMetadata() = """
    {
        "status": "ok",
        "message": "Not found",
        "time": 1607963039390,
        "isTag": true,
        "commit": "9a1e4c49b5da159cfdff260e36648a8a18540d4a",
        "private": false,
        "version": "1.0.1"
    }
""".trimIndent()

internal val filteredFakeJitPackSerializedResponse = """
    {
        "com.github.zhuinden:simple-stack-extensions": [],
        "com.github.zhuinden:simple-stack": [],
        "com.github.zhuinden:state-bundle": [],
        "com.github.zhuinden:realm-monarchy": [],
        "com.github.zhuinden:fragmentviewbindingdelegate-kt": []
    }
""".trimIndent()

internal val filteredFakeJitPackResponseMetadata = buildList { repeat(5) { add(produceFakeMetadata()) } }

internal val filteredFakeJitPackDeSerializedResponse = JitPackResponse(
    listOf(
        JitPackArtifact("com.github.zhuinden:simple-stack-extensions", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.zhuinden:simple-stack", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.zhuinden:state-bundle", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.zhuinden:realm-monarchy", "1.0.1", 1607963039390),
        JitPackArtifact("com.github.zhuinden:fragmentviewbindingdelegate-kt", "1.0.1", 1607963039390),
    )
)

internal val fakeMavenCentralDeserializedResponse = MavenCentralResponse(
    responseHeader = MavenCentralResponseHeader(),
    response = ResponseBody(
        numFound = 2,
        start = 0,
        listOf(
            MavenCentralArtifact(
                "io.github.a914-gowtham:compose-ratingbar",
                "io.github.a914-gowtham",
                "compose-ratingbar",
                "1.2.3",
                "aar",
                1644000428000
            ),
            MavenCentralArtifact(
                "de.charlex.compose:speeddial",
                "de.charlex.compose",
                "speeddial",
                "1.0.0-beta03",
                "aar",
                1623868446000
            )
        )
    )
)
