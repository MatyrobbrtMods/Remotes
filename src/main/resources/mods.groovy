ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[40,)'

    license = 'MIT'
    issueTrackerUrl = 'https://github.com/MatyrobbrtMods/Remotes/issues'

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