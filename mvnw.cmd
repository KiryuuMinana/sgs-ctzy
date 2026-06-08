@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.2.0
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP__=
@SET PSModulePath=
@SET MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
@IF NOT "%MAVEN_PROJECTBASEDIR%"=="" GOTO endDetectBaseDir
@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties" GOTO endDetectBaseDir
@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO @(
    IF "%%A"=="basedir" SET MAVEN_PROJECTBASEDIR=%%B
)
:endDetectBaseDir
@IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
    @ECHO Maven Wrapper JAR not found at "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
    @ECHO Downloading...
    @SET MAVEN_WRAPPER_JAR_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
    @FOR /F "tokens=2 delims==" %%A IN ('findstr /R "^wrapperUrl" "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"') DO @SET MAVEN_WRAPPER_JAR_URL=%%A
    powershell -Command "& {Invoke-WebRequest -Uri '%MAVEN_WRAPPER_JAR_URL%' -OutFile '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar'}"
)
@IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
    @ECHO Failed to download Maven Wrapper JAR
    @EXIT /B 1
)
@SET MAVEN_WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
@SET MAVEN_WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

%MAVEN_JAVA_EXE% ^
  %JVM_CONFIG_MAVEN_PROPS% ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %MAVEN_WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %MAVEN_WRAPPER_LAUNCHER% %MAVEN_CMD_LINE_ARGS%

@if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@if "%ERROR_CODE%"=="" set ERROR_CODE=0
@IF NOT "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@FOR /F "tokens=2 delims==" %%A IN ('findstr /R "^#env" "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" 2^>nul') DO @(
    IF "%%A"=="skip" SET MAVEN_SKIP_RC=skip
)
@if NOT "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@SET "JRE_HOME=%JAVA_HOME%"
@if NOT "%JRE_HOME%"=="" goto skipRcPost
@SET "JRE_HOME=%MAVEN_JAVA_EXE%\.."

:skipRcPost
@EXIT /B %ERROR_CODE%
