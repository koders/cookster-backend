package lv.cookster.entity;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class OperationResult {

    private Integer operationCode;
    private Integer resultCode;
    private String message;

    public Integer getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(Integer operationCode) {
        this.operationCode = operationCode;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OperationResult result = (OperationResult) o;

        if (message != null ? !message.equals(result.message) : result.message != null) {
            return false;
        }
        if (operationCode != null ? !operationCode.equals(result.operationCode) : result.operationCode != null) {
            return false;
        }
        if (resultCode != null ? !resultCode.equals(result.resultCode) : result.resultCode != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = operationCode != null ? operationCode.hashCode() : 0;
        result = 31 * result + (resultCode != null ? resultCode.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}

