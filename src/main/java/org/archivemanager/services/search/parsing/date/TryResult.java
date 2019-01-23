package org.archivemanager.services.search.parsing.date;


/**
* Created by babar on 1/1/14.
*/
public final class TryResult<ResultType>{

    private final ResultType value;

    public TryResult(ResultType value){
        this.value =value;
    }

    public TryResult() {
        value = null;
    }

    public ResultType getValueOrElse(ResultType elseValue){
        return value != null ? value : elseValue;
    }

    public ResultType getValueOrElse(Function<ResultType> function){
        return value != null ? value : function.apply();
    }

    public void useValueIfAny(ValueGetter<ResultType> valueGetter) {
        if(value != null) valueGetter.getValue(value);
    }
}
