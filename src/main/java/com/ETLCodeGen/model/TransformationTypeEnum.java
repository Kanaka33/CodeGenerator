package com.ETLCodeGen.model;

public enum TransformationTypeEnum {
    Source_Definition,
    Source_Qualifier,
    Expression,
    Target_Definition;

    public String getName(){
        return this.name();
    }

}
