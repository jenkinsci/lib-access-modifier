def text = new File(basedir, 'build.log').text
assert text.contains('[ERROR] caller/Caller:11 api/impl/NotApi must not be used')
assert text.contains('[ERROR] caller/Caller:12 api/impl/NotApi must not be used')
assert text.contains('[ERROR] caller/Caller:13 api/impl/NotApi must not be used')
assert text.contains('[ERROR] caller/Caller:14 api/impl/NotApi must not be used')
