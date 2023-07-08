package filemanager.com.client;

/**A response from server
 * 
 * @author triet
 *
 */
public class Response {
	private boolean status;
	private String message;
	
	public Response() {}

	public Response(boolean status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	public boolean getStatus() {
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
	
	public void setResponse(boolean status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public String toString() {
		return String.format("Status: %d\n%s", status ? 1 : 0, message);
	}
	
	
}
