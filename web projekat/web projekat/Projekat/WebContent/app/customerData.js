Vue.component("customerData", {
	data: function(){
		return {
			username: "",
			password: "",
			firstName: "",
			lastName: "",
			gender: "",
			dateOfBirth: "",
			userRole: "",
			collectedPoint: "",
			customerType: "",
			sale: "",
			requiredPoints: "",
			blocked: "",
			error: false,
			errorText: "",
		}
		
	},
	template:
	`
	<div style="background-color:#009879">
		<div class="topnav">
		<br>
			<div style="background-color: white">
				<button v-on:click="homePage()">
					<img src="images/profile.png" style =" width: 45px; height : 45px; padding-top:5px;">
				</button>
				<h2 style="padding-left: 80x; padding-top: 5px">Podaci o korisniku</h2>
				<button style="color:blue; padding-top:5 px; padding-right: 10px; float: right;" v-on:click="logOut"> Odjavi se! </button>
			</div>
		<br>
		</div>
		<div class="wrapper5">
    		<div class="form">
       			<div class="inputfield">
          			<label>Ime </label>
          			<input type="text" class="input" v-model="firstName" required>
       			</div>  
        		<div class="inputfield">
          			<label>Prezime</label>
          			<input type="text" class="input" v-model="lastName"  required>
       			</div>  
				<div class="inputfield">
          			<label>Korisnicko ime</label>
          			<input type="text" class="input" v-model="username" required>
       			</div> 
      		 	<div class="inputfield">
          			<label>Lozinka</label>
          			<input type="password" class="input" v-model="password" required>
       			</div>  
        		<div class="inputfield">
          			<label>Pol</label>
          			<div class="custom_select">
            			<select  v-model="gender" >
              			<option selected value="MUSKI"> Musko </option>
             			 <option value="ZENSKI"> Zensko </option>
            			</select>
          			</div>
       			</div> 
        		<div class="inputfield">
         			<label>Datum rodjenja</label>
          			<input type="text" class="input" v-model="dateOfBirth" placeholder="Format: yyyy-MM-dd" required>
       			</div> 
				<div class="inputfield">
         			<label>Uloga korisnika</label>
          			<input type="text" class="input" v-model="userRole" disabled required>
       			</div> 
				<div class="inputfield">
         			<label>Tip kupca</label>
          			<input type="text" class="input" v-model="customerType" disabled required>
       			</div> 
      		<div class="inputfield">
				<input type="button" v-on:click="goBack()" value="Odustani" style ="margin-right: 5px" class="btn">
       		 	<input type="submit" v-on:click="editUser()" style ="margin-left: 5px" class="btn">
      		</div>
	  <div class="inputfield">
		<p style="color:red;" v-if="error">{{errorText}} </p>
      </div>
    </div>
</div> 
	</div>
	`,
	methods:{
		goBack: function() {
			this.$router.push("/homeCustomer")
		},
		homePage: function(){
			this.$router.push("/homeCustomer")
		},
		logOut: function() {
			axios
				.get('rest/users/logOutUser')
				.then(alert('Odjavljen korisnik'))
			this.$router.push("/");
		},
		editUser: function(){
				if (this.firstName != "" && this.lastName != "" && this.gender != "" && this.dateOfBirth != "" && this.username != "" && this.password != "") {
				if (this.dateOfBirth.length == 10 && this.dateOfBirth.charAt(4) == '-' && this.dateOfBirth.charAt(7) == '-') {
						this.error = false;
						var user = {
							"username": this.username,
							"password": this.password,
							"firstName": this.firstName,
							"lastName": this.lastName,
							"gender": this.gender,
							"dateOfBirth": this.dateOfBirth
						};
						axios
							.put("rest/users/editUser", user)
							.then(this.$router.push('/homeCustomer'))
					}
				else {
					this.error = true;
					this.errorText = "Datum nije unet u ispravnom formatu.";
				}
			}
			else {
				this.error = true;
				this.errorText = "Neophodno je uneti sve podatke.";
			}
		}
	},
	mounted() {
		alert()
		axios
			.get('rest/users/getUserData')
			.then(response => { this.username = response.data.username; this.firstName=response.data.firstName; this.lastName=response.data.lastName; this.password=response.data.password; this.gender=response.data.gender; this.dateOfBirth=response.data.dateOfBirth; this.userRole=response.data.userRole; this.customerType=response.data.customerType ;this.ready = true });
	},
});