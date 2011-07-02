<%@ page contentType="application/xrds+xml"%>
<xrds:XRDS xmlns:xrds="xri://$xrds"
    xmlns:openid="http://openid.net/xmlns/1.0" xmlns="xri://$xrd*($v*2.0)">
    <XRD>
        <Service priority="0">
            <Type>http://specs.openid.net/auth/2.0/server</Type>
            <Type>http://specs.openid.net/extensions/pape/1.0</Type>
            <Type>http://openid.net/srv/ax/1.0</Type>
            <Type>http://specs.openid.net/extensions/oauth/1.0</Type>
            <Type>http://specs.openid.net/extensions/ui/1.0/lang-pref</Type>
            <Type>http://specs.openid.net/extensions/ui/1.0/mode/popup</Type>
            <Type>http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier</Type>
            <Type>http://www.idmanagement.gov/schema/2009/05/icam/no-pii.pdf</Type>
            <Type>http://www.idmanagement.gov/schema/2009/05/icam/openid-trust-level1.pdf</Type>
            <Type>http://csrc.nist.gov/publications/nistpubs/800-63/SP800-63V1_0_2.pdf</Type>
            <URI><%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()%>/provider/endpoint</URI>
        </Service>
    </XRD>
</xrds:XRDS>
