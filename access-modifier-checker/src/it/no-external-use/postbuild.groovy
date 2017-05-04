assert new File(basedir, 'build.log').text.contains('[ERROR] caller/Caller:8 api/Api.notReallyPublic()V must not be used')
