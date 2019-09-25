/**
 * API
 */

window.API_1484_11 = {
		
		"Initialize" : function(arg) { 
			console.log("initialize")
			return "true"
		},
		"Terminate" : function(arg) {
			console.log("terminate")
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
			return "true"
		},
		"Commit" : function(arg) {
			console.log("commit")
			return "true"
		},
		"GetLastError" : function() {
			return "0"
		}
		
}