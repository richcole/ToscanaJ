<?xml version="1.0" encoding="ISO-8859-1"?>
<conceptualSchema version="1.0" askDatabase="true" xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://meganesia.int.gu.edu.au/projects/ToscanaJ/schemas/csx.xsd">
        <context>
                <object id="1">([m�nnlich]='x' OR [m�nnlich]='X') AND ([weiblich]='.' OR ([weiblich] IS NULL))</object>
                <object id="2">([m�nnlich]='.' OR ([m�nnlich] IS NULL)) AND ([weiblich]='x' OR [weiblich]='X')</object>
                <object id="3">([beruf]='x' OR [beruf]='X') AND ([student]='.' OR ([student] IS NULL)) AND ([sch�ler]='.' OR ([sch�ler] IS NULL))</object>
                <object id="4">([beruf]='.' OR ([beruf] IS NULL)) AND ([student]='x' OR [student]='X') AND ([sch�ler]='.' OR ([sch�ler] IS NULL))</object>
                <object id="5">([beruf]='.' OR ([beruf] IS NULL)) AND ([student]='.' OR ([student] IS NULL)) AND ([sch�ler]='x' OR [sch�ler]='X')</object>
                <attribute id="1">m�nnlich</attribute>
                <attribute id="2">weiblich</attribute>
                <attribute id="3">beruf</attribute>
                <attribute id="4">student</attribute>
                <attribute id="5">sch�ler</attribute>
        </context>
        <diagram title="Geschlecht">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent/>
                        <attributeContingent/>
                </concept>
                <concept id="2">
                        <position x="10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>1</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="3.933330" y="4.300000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>1</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="-10.000000" y="-10.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="-1.133330" y="-4.300000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <objectRef>2</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-3.233330" y="4.600000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>2</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent/>
                        <attributeContingent/>
                </concept>
                <edge from="1" to="2"/>
                <edge from="1" to="3"/>
                <edge from="2" to="4"/>
                <edge from="3" to="4"/>
        </diagram>
        <diagram title="Status">
                <concept id="1">
                        <position x="0.000000" y="0.000000"/>
                        <objectContingent/>
                        <attributeContingent/>
                </concept>
                <concept id="2">
                        <position x="20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>3</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="7.500000" y="4.600000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>3</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="3">
                        <position x="0.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="3.000000" y="-3.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <objectRef>4</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="13.800000" y="13.000000"/>
                                        <textAlignment>left</textAlignment>
                                </labelStyle>
                                <attributeRef>4</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="4">
                        <position x="-20.000000" y="-20.000000"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="-5.200000" y="-5.100000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <objectRef>5</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <labelStyle>
                                        <offset x="-6.700000" y="4.100000"/>
                                        <textAlignment>right</textAlignment>
                                </labelStyle>
                                <attributeRef>5</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="5">
                        <position x="0.000000" y="-40.000000"/>
                        <objectContingent/>
                        <attributeContingent/>
                </concept>
                <edge from="1" to="2"/>
                <edge from="1" to="3"/>
                <edge from="1" to="4"/>
                <edge from="2" to="5"/>
                <edge from="3" to="5"/>
                <edge from="4" to="5"/>
        </diagram>
</conceptualSchema>