assert new File(basedir, 'build.log').text.contains('[WARNING] caller/Caller:8 api/Api.notReallyPublic()V must not be used')
