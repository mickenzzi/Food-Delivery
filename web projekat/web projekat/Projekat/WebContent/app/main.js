Vue.component("home", {
	data: function() {
		return {
			users: null,
			username: "",
			password: "",
			title: "Login",
			error: false,
			blocked_user: false,
			errorText: "Neispravno korisnicko ime i/ili lozinka.",
		}
	},
	template:
		`
	
	<div class="bg-gradient">
		<div class="sticky-header">
			<div>
				<label class="sticky-header-title">Dostavljac.com</label>
			</div>
		</div>
		<div class="wrap-image">
			<img style="height: 140px;
	width: 140px;
	margin-left: 40px;" src="images/food.png">
			</div>
		<div class="container-image">
			
		</div>
		<form class="container-login100">
			<div class="wrap-login100" >
				<div class="login100-form">
					<span class="login100-form-title p-b-26">
						Welcome!
					</span>

					<div class="wrap-input100">
						<input class="input100" type="text" v-model="username" placeholder="Username">
							<span class="focus-input100" data-placeholder="Username"></span>
					</div>

					<div class="wrap-input100">
						<input class="input100" type="password" v-model="password" placeholder="Password">
						<span class="focus-input100" data-placeholder="Password"></span>
					</div>
						<p style="color:red;" v-if="error">{{errorText}} </p>
						
				</div>
				<div class="container-login100-form-btn">
				 	<div class="wrap-login100-form-btn">
						<div class="login100-form-bgbtn"></div>
						<button type="button" class="login100-form-btn"  v-on:click="login">
							Login
						</button>
					</div>
				</div>
				<div style="margin-top: 30px; margin-left: 9%; font-size: 15px;">
					<span >
						Don't have an account? 
					</span>
					<router-link to="/registration" id="registration">Sign up!</router-link>
				</div>
				<div style="margin-top: 5px; margin-left: 20%; font-size: 15px;">
					<button style="color:blue" v-on:click="goToGuest()">Continue as guest! </button>
				</div>
			</div>
		</form>
	</div>
	`,
	methods: {
		login: function() {
			var u = { "username": this.username, "password": this.password }
			axios
				.post('rest/users/loginUser', u)
				.then(this.redirection())
				.catch(response => { this.error = true; })
		},
		goToGuest: function(){
			this.$router.push('/guestHome')
		},
		redirection: function() {
			for (let u of this.users) {
				if (u.username == this.username) {
					if (u.password == this.password) {
						this.error = false
						if (u.blocked == false && u.deleted == false) {
							if (u.role == "CUSTOMER") {
								this.$router.push("/homeCustomer");
							}else if(u.role == "ADMINISTRATOR"){
								this.$router.push("/administrator");
							}
						} else if (u.blocked == true) {
							this.greska = true;
							this.txtGreska = "Vas nalog je blokiran";
						}
						else if (u.deleted == true) {
							this.greska = true;
							this.txtGreska = "Vas nalog je obrisan";
						}
					}
				}
			}
		},
	},
	mounted() {
		axios
			.get('rest/users')
			.then(response => (this.users = response.data));
	},
});
