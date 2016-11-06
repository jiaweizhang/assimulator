package assimulator.utilities;

import assimulator.StdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by jiaweizhang on 11/6/2016.
 */
public class ResponseUtility {
	public static ResponseEntity wrap(StdResponse stdResponse) {
		stdResponse.timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
		switch (stdResponse.status) {
			case 200:
				return ResponseEntity.status(HttpStatus.OK).body(stdResponse);
			case 403:
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(stdResponse);
			case 500:
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stdResponse);
			default:
				return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(stdResponse);
		}
	}
}
