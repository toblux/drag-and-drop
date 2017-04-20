debug:
	lein cljsbuild once debug

release:
	lein cljsbuild once release

figwheel:
	lein figwheel

clean:
	lein clean

.PHONY: debug release figwheel clean
