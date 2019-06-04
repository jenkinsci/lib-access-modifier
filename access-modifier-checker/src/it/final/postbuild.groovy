assert new File(basedir, 'build.log').text.contains('[ERROR] impl2/Impl2:10 api/Api.notReallyFinal()V must not be overridden. Because we say so')
