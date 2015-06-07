@echo off
REM Uncomment Location the line that represents where you installed Genesis
REM and comment out (or delete)the line that currently sets the location
REM The default location is the current directory (".")

set Location="."

REM set Location="C:\program files\genesis"
REM set Location="U:\genesis"

java  -cp %Location%  GenesisInterpreter %1 %2 %3 %4 %5 %6 %7 %8 %9
