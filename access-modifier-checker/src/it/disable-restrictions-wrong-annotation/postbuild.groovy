assert new File(basedir, 'build.log').text.contains('[ERROR] caller/Caller:9 api/ApiWithRestrictedMethodAndField.notReallyPublic()V must not be used')
assert new File(basedir, 'build.log').text.contains('[ERROR] caller/CallerDisabledAtClassLevel:8 api/ApiWithRestrictedMethodAndField.notReallyPublic()V must not be used')
