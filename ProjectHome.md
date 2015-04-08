Eclipse plugin to develop SAP Portal iView.

It does not provide all the functionality of NWDS from SAP.

Notable features include:

  * Based on Eclipse 3 upwards.
  * Can export to PAR file and upload to SAP EP.
  * Wizard to create a PDK project.
  * Preference page to specify SAP Portal servers to upload your par file.

Update Site: http://dev.bpnr.co.kr/public/simplepdk/updatesite

To setup your development environment,

  1. To run eclipse 3, you need java 6. Install JDK6.
  1. To compile your code, you need jdk 1.4. Install jdk 1.4
  1. Download and run eclipse.
  1. Install simplepdk plugin using updatesite.
  1. You need to set CLASSPATH Variable 'SAPPORTAL\_LIBS' pointing to the folder which contains all of SAP Portal library jars.