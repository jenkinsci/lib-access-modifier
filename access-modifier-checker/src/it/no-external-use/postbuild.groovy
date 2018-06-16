assert new File(basedir, 'build.log').text.contains('[ERROR] caller/Caller:9 api/Api.notReallyPublic()V must not be used')
assert new File(basedir, 'build.log').text.contains('[ERROR] caller/Caller:10 foo must not be used')
