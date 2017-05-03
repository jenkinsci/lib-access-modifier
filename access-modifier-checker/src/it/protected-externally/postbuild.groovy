assert new File(basedir, 'build.log').text.contains('[ERROR] caller2/Caller2:9 api/Api.notReallyPublic()V must not be called except as if protected')
