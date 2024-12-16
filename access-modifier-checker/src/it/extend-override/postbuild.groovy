assert new File(basedir, 'build.log').text.contains('api/Api.notReallyPublic()V must not be used')
