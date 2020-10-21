#!/bin/bash

function one_line_pem {
    echo "`awk 'NF {sub(/\\n/, ""); printf "%s\\\\\\\n",$0;}' $1`"
}

function json_ccp {
    local PP=$(one_line_pem $5)
    local CP=$(one_line_pem $6)
    sed -e "s/\${ORG}/$1/" \
        -e "s/\${P0PORT}/$2/" \
        -e "s/\${P1PORT}/$3/" \
        -e "s/\${CAPORT}/$4/" \
        -e "s#\${PEERPEM}#$PP#" \
        -e "s#\${CAPEM}#$CP#" \
        ccp-template.json 
}

function yaml_ccp {
    local PP=$(one_line_pem $5)
    local CP=$(one_line_pem $6)
    sed -e "s/\${ORG}/$1/" \
        -e "s/\${P0PORT}/$2/" \
        -e "s/\${P1PORT}/$3/" \
        -e "s/\${CAPORT}/$4/" \
        -e "s#\${PEERPEM}#$PP#" \
        -e "s#\${CAPEM}#$CP#" \
        ccp-template.yaml | sed -e $'s/\\\\n/\\\n        /g'
}

function json_ccp_custom {
    local PP1=$(one_line_pem $5)
    local CP1=$(one_line_pem $6)
	local PP2=$(one_line_pem ${11})
    local CP2=$(one_line_pem ${12})
    sed -e "s/\${ORG1}/$1/" \
        -e "s/\${P0PORT1}/$2/" \
        -e "s/\${P1PORT1}/$3/" \
        -e "s/\${CAPORT1}/$4/" \
        -e "s#\${PEERPEM1}#$PP1#" \
        -e "s#\${CAPEM1}#$CP1#" \
		-e "s/\${ORG2}/$7/" \
        -e "s/\${P0PORT2}/$8/" \
        -e "s/\${P1PORT2}/$9/" \
        -e "s/\${CAPORT2}/${10}/" \
        -e "s#\${PEERPEM2}#$PP2#" \
        -e "s#\${CAPEM2}#$CP2#" \
        ccp-template-custom.json 
}

ORG=1
P0PORT=7051
P1PORT=8051
CAPORT=7054
PEERPEM=crypto-config/peerOrganizations/org1.example.com/tlsca/tlsca.org1.example.com-cert.pem
CAPEM=crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem

echo "$(json_ccp $ORG $P0PORT $P1PORT $CAPORT $PEERPEM $CAPEM)" > connection-org1.json
echo "$(yaml_ccp $ORG $P0PORT $P1PORT $CAPORT $PEERPEM $CAPEM)" > connection-org1.yaml

ORG=2
P0PORT=9051
P1PORT=10051
CAPORT=8054
PEERPEM=crypto-config/peerOrganizations/org2.example.com/tlsca/tlsca.org2.example.com-cert.pem
CAPEM=crypto-config/peerOrganizations/org2.example.com/ca/ca.org2.example.com-cert.pem

echo "$(json_ccp $ORG $P0PORT $P1PORT $CAPORT $PEERPEM $CAPEM)" > connection-org2.json
echo "$(yaml_ccp $ORG $P0PORT $P1PORT $CAPORT $PEERPEM $CAPEM)" > connection-org2.yaml

ORG1=1
P0PORT1=7051
P1PORT1=8051
CAPORT1=7054
PEERPEM1=crypto-config/peerOrganizations/org1.example.com/tlsca/tlsca.org1.example.com-cert.pem
CAPEM1=crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem
ORG2=2
P0PORT2=9051
P1PORT2=10051
CAPORT2=8054
PEERPEM2=crypto-config/peerOrganizations/org2.example.com/tlsca/tlsca.org2.example.com-cert.pem
CAPEM2=crypto-config/peerOrganizations/org2.example.com/ca/ca.org2.example.com-cert.pem

echo "$(json_ccp_custom $ORG1 $P0PORT1 $P1PORT1 $CAPORT1 $PEERPEM1 $CAPEM1 $ORG2 $P0PORT2 $P1PORT2 $CAPORT2 $PEERPEM2 $CAPEM2)" > connection-custom.json
