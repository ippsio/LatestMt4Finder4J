@echo off
SET _EXE=LatestMt4Finder.exe
SET _WORKDIR=..\workspace\LatestMt4Finder
SET _LIBSDIR=..\workspace\lib\thisiscool-gcc\swt\win32\3218
SET _DESTDIR=..\workspace\LatestMt4Finder\dest

SET _OPTIMIZE_OPTION=-Os -s
SET _DFILE_ENCODE=-Dfile.encoding=SJIS
SET _DENCODE=--encoding=SJIS
@echo on

gcj -fjni %_OPTIMIZE_OPTION% %_DFILE_ENCODE% %_DENCODE% --main=com.hoge.fx.FindLatestMt4 -o %_DESTDIR%/%_EXE% %_DESTDIR%/LatestMt4Finder_fat.jar
@copy %_LIBSDIR%\swt-gdip-win32-3218.dll %_DESTDIR%\
@copy %_LIBSDIR%\swt-win32-3218.dll %_DESTDIR%\

@%_DESTDIR%\upx --best %_DESTDIR%/%_EXE%
@echo DONE!

%_DESTDIR%\%_EXE%
pause
