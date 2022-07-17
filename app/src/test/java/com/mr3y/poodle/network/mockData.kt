package com.mr3y.poodle.network

import com.mr3y.poodle.network.models.JitPackArtifact
import com.mr3y.poodle.network.models.JitPackResponse
import com.mr3y.poodle.network.models.MavenCentralArtifact
import com.mr3y.poodle.network.models.MavenCentralResponse

internal val fakeMavenCentralSerializedResponse = """
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
""".trimIndent()

internal val fakeJitPackSerializedResponse = """
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
    }
""".trimIndent()

internal val fakeJitPackDeserializedResponse = JitPackResponse(
    listOf(
        JitPackArtifact("com.github.contentful:rich-text-renderer-java"),
        JitPackArtifact("com.github.texttechnologylab:textimager-uima"),
        JitPackArtifact("com.github.santalu:mask-edittext"),
        JitPackArtifact("com.github.poovamraj:pinedittextfield"),
        JitPackArtifact("com.github.ramseth001:textdrawable"),
        JitPackArtifact(
            "com.github.cielsk:clearable-edittext"
        ),
        JitPackArtifact(
            id =
            "com.github.blackcat27:currencyedittext"
        ),
        JitPackArtifact(
            id =
            "com.github.lygttpod:supertextview"
        ),
        JitPackArtifact(
            id =
            "com.github.viksaaskool:autofitedittext"
        ),
        JitPackArtifact(
            id =
            "com.github.scottyab:showhidepasswordedittext"
        ),
        JitPackArtifact(
            id =
            "com.github.hitgif:textfieldboxes"
        ),
        JitPackArtifact(
            id =
            "com.github.androiddeveloperlb:autofittextview"
        ),
        JitPackArtifact(
            id =
            "com.github.yvescheung:rollingtext"
        ),
        JitPackArtifact(
            id =
            "com.github.iambedant:outlinetextview"
        ),
        JitPackArtifact(
            id =
            "com.github.carterhudson:html-textview"
        ),
        JitPackArtifact(
            id =
            "com.github.mzcretin:expandabletextview"
        ),
        JitPackArtifact(
            id =
            "com.github.chenbingx:supertextview"
        ),
        JitPackArtifact(
            id =
            "com.github.mahimrocky:showmoretext"
        ),
        JitPackArtifact(
            id =
            "com.andreabaccega:android-form-edittext"
        ),
        JitPackArtifact(
            id =
            "com.github.mabbas007:tagsedittext"
        ),
        JitPackArtifact(
            id =
            "com.github.xiaweizi:marqueetextview"
        ),
        JitPackArtifact(
            id =
            "com.github.swapnil1104:otpedittext"
        ),
        JitPackArtifact(
            id =
            "com.github.deano2390:flowtextview"
        ),
        JitPackArtifact(
            id =
            "com.github.amulyakhare:textdrawable"
        ),
        JitPackArtifact(
            id =
            "com.github.wednesday-solutions:creditcardedittext"
        ),
        JitPackArtifact(
            id =
            "com.github.egslava:edittext-mask"
        ),
        JitPackArtifact(
            id =
            "com.github.victorminerva:autoresizeedittext"
        ),
        JitPackArtifact(
            id =
            "com.github.wordpress-mobile:react-native-safe-area-context"
        ),
        JitPackArtifact(
            id =
            "com.github.dimorinny:floating-text-button"
        ),
        JitPackArtifact(
            id =
            "com.github.tobiasschuerg:android-prefix-suffix-edit-text"
        ),
        JitPackArtifact(
            id =
            "com.github.kyash:validatable-textinput-layout"
        ),
        JitPackArtifact(
            id =
            "com.github.ammargitham:autolinktextviewv2"
        ),
        JitPackArtifact(
            id =
            "com.github.btranslations:text-processing-utils"
        ),
        JitPackArtifact(
            id =
            "io.github.hakky54:sslcontext-kickstart"
        ),
        JitPackArtifact(
            id =
            "com.github.johnkil:android-robototextview"
        ),
        JitPackArtifact(
            id =
            "com.github.wizenoze:justext-java"
        ),
        JitPackArtifact(
            id =
            "com.github.mukeshsolanki:google-places-autocomplete-edittext"
        ),
        JitPackArtifact(
            id =
            "com.github.vilyever:androidcontextholder"
        ),
        JitPackArtifact(
            id =
            "me.austinhuang:autolinktextviewv2"
        ),
        JitPackArtifact(
            id =
            "com.github.apg-mobile:android-round-textview"
        ),
        JitPackArtifact(
            id =
            "com.github.gcx-hci:grandcentrix-formatted-text-android"
        ),
        JitPackArtifact(
            id =
            "com.github.paradoxie:autoverticaltextview"
        ),
        JitPackArtifact(
            id =
            "com.github.limedroid:xrichtext"
        ),
        JitPackArtifact(
            id =
            "com.github.jmperezra:highlighttextview"
        ),
        JitPackArtifact(
            id =
            "com.github.ilgun:expandingtextarea"
        ),
        JitPackArtifact(
            id =
            "com.github.sendtion:xrichtext"
        ),
        JitPackArtifact(
            id =
            "com.github.tylersuehr7:social-text-view"
        ),
        JitPackArtifact(
            id =
            "com.github.elevenetc:textsurface"
        ),
        JitPackArtifact(
            id =
            "com.github.blackboxvision:datetimepicker-edittext"
        ),
        JitPackArtifact(
            id =
            "com.github.yalantis:context-menu.android"
        )
    )
)

internal val filteredFakeJitPackSerializedResponse = """
    {
        "com.github.zhuinden:simple-stack-extensions": [],
        "com.github.zhuinden:simple-stack": [],
        "com.github.zhuinden:state-bundle": [],
        "com.github.zhuinden:realm-monarchy": [],
        "com.github.zhuinden:fragmentviewbindingdelegate-kt": []
    }
""".trimIndent()

internal val filteredFakeJitPackDeSerializedResponse = JitPackResponse(
    listOf(
        JitPackArtifact("com.github.zhuinden:simple-stack-extensions"),
        JitPackArtifact("com.github.zhuinden:simple-stack"),
        JitPackArtifact("com.github.zhuinden:state-bundle"),
        JitPackArtifact("com.github.zhuinden:realm-monarchy"),
        JitPackArtifact("com.github.zhuinden:fragmentviewbindingdelegate-kt"),
    )
)

internal val fakeMavenCentralDeserializedResponse = MavenCentralResponse(
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
