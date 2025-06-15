@echo off
setlocal enabledelayedexpansion

set count=1

for %%f in (*.png) do (
    set "num=00!count!"
    set "num=!num:~-2!"
    ren "%%f" "changenamehere_!num!.png"
    set /a count+=1
)

echo Done renaming files.
pause