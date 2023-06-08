package filemanager.com.server;

public class Response {
	private boolean status;
	private String message;
	
	public Response() {
		setStatus(false);
	}
	
	public Response(boolean status, String message) {
		setResponse(status, message);
	}
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setResponse(boolean status, String response) {
		setStatus(status);
		setMessage(message);
	}
	
}
