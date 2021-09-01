Vue.component("administrator", {
	data: function() {
		
	},
	template:
		`
	<div class="topnav">
  <a class="active" href="#home">Home</a>
  <a href="#news">News</a>
  <a href="#contact">Contact</a>
  <a href="#about">About</a>
	</div>
		
	  ` ,

	
	methods: {
		registratitonUser: function() {
			var u = {
				"username": this.username,
				"password": this.password1,
				"firstName": this.firstName,
				"lastName": this.lastName,
				"dateOfBirth": this.dateOfBirth
			};
			console.log(u);
			axios
				.post("rest/users/registrationCustomer", u)
		},
		homePage: function() {
			this.$router.push("/")
		}
	},
	mounted() {
		//axios
		//	.get('rest/users')
		//	.then(response => (this.users = response.data));
	},
});