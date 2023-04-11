ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[40,)'

    license = 'MIT'
    // A URL to refer people to when problems occur with this mod
    issueTrackerUrl = 'https://change.me.to.your.issue.tracker.example.invalid/'

    mod {
        modId = 'remotes'
        displayName = 'Remotes'
		displayTest = 'MATCH_VERSION'

        version = this.version

        description = '''A mod that adds remotes'''
        authors = ['Matyrobbrt']

        // logoFile = 'remotes.png'

        dependencies {
            forge = "[${this.forgeVersion},)"
            minecraft = this.minecraftVersionRange
        }
    }
}