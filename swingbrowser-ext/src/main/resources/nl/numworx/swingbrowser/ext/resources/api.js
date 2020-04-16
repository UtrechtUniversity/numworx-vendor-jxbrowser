/**
 * API
 */

window.API_1484_11 = {
		"values": { },
		
		"Initialize" : function(arg) { 
			console.log("initialize")
			return "true"
		},
		"Terminate" : function(arg) {
			console.log("terminate")
			var xhr = new XMLHttpRequest();
			xhr.open("POST", "Terminate", true);
			xhr.setRequestHeader('Content-Type', 'application/json');
			xhr.send(JSON.stringify(this.values));
			return "true"
		},
		"GetValue" : function(name) {
			console.log("get value " + name);
			var val = scorm[name];
			if (typeof val !== 'undefined') {
				console.log("value = " + val);
				return val;
			}
			return "";
		},
		"SetValue" : function(name, value) {
			console.log("set value " + name + " = " + value)
			this.values[name] = value;
			scorm[name] = value;
			return "true"
		},
		"Commit" : function(arg) {
			console.log("commit")
			var xhr = new XMLHttpRequest();
			xhr.open("POST", "Commit", true);
			xhr.setRequestHeader('Content-Type', 'application/json');
			xhr.send(JSON.stringify(this.values));
			return "true"
		},
		"GetLastError" : function() {
			return "0"
		}
		
}