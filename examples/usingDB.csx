<?xml version="1.0" encoding="UTF-8"?>
<conceptualSchema version="1.0" askDatabase="true" xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://meganesia.int.gu.edu.au/projects/ToscanaJ/schemas/csx.xsd">
        <database>
                <path>C:\hinz\und\kunz</path>
                <query>SELECT field FROM table</query>
        </database>
        <viewer>
                <program>notepad</program>
                <template>simpleText.txt</template>
                <openDelimiter>%</openDelimiter>
                <closeDelimiter>#</closeDelimiter>
        </viewer>
        <context>
                <object id="1">field2=3</object>
                <object id="2">field2=8</object>
                <attribute id="1">Attribute One</attribute>
                <attribute id="2">Attribute Two</attribute>
        </context>
        <diagram title="Diagram 1">
                <concept id="1">
                        <position x="1.0" y="2.3"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="1.0" y="2.0"/>
                                        <textColor>#FFFFFF</textColor>
                                        <bgColor>#123456</bgColor>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>1</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <attributeRef>1</attributeRef>
                        </attributeContingent>
                </concept>
                <concept id="2">
                        <position x="1.0" y="7.8"/>
                        <objectContingent>
                                <labelStyle>
                                        <offset x="1.0" y="2.0"/>
                                        <textColor>#123456</textColor>
                                        <bgColor>#FFFFFF</bgColor>
                                        <textAlignment>center</textAlignment>
                                </labelStyle>
                                <objectRef>2</objectRef>
                        </objectContingent>
                        <attributeContingent>
                                <attributeRef>2</attributeRef>
                        </attributeContingent>
                </concept>
                <edge from="1" to="2"/>
        </diagram>
</conceptualSchema>