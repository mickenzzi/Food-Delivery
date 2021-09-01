Vue.component("registration", {
	data: function() {
		return {
			firstName: "",
			lastName: "",
			dateOfBirth: "",
			gender: "",
			password1: "",
			password2: "",
			username: "",
			error: false,
			errorText: "Sva polja su obavezna",
			users: null,
		}
	},
	template:
		`
	<div style="background-color:009879">
	<div class="wrapper5">
    <div class="title">
     Registracija novog kupca
    </div>
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
          <input type="password" class="input" v-model="password1" required>
       </div>  
      <div class="inputfield">
          <label>Potvrdi lozinku</label>
          <input type="password" class="input"  v-model="password2" required>
       </div> 
        <div class="inputfield">
          <label>Pol</label>
          <div class="custom_select">
            <select  v-model="gender" >
              <option selected value="MUSKI"> Muski </option>
              <option value="ZENSKI"> Zenski </option>
            </select>
          </div>
       </div> 
        <div class="inputfield">
          <label>Datum rodjenja</label>
          <input type="text" class="input" v-model="dateOfBirth" placeholder="Format: yyyy-MM-dd" required>
       </div> 
      <div class="inputfield">
		<input type="button" @click="homePage" value="Odustani" style ="margin-right: 5px" class="btn">
        <input type="submit" @click = "registrationCustomer" value="Dodaj" style ="margin-left: 5px" class="btn">
      </div>
	  <div class="inputfield">
		<p style="color:red;" v-if="error">{{errorText}} </p>
      </div>
    </div>
</div> 
</div>
	  ` ,
	methods: {
		registrationCustomer: function() {
			if (this.firstName != "" && this.lastName != "" && this.gender != "" && this.dateOfBirth != "" && this.username != "" && this.password1 != "" && this.password2 != "") {
				if (this.dateOfBirth.length == 10 && this.dateOfBirth.charAt(4) == '-' && this.dateOfBirth.charAt(7) == '-') {
					if (this.password1 == this.password2) {
						this.error = false;
						var user = {
							"username": this.username,
							"password": this.password1,
							"firstName": this.firstName,
							"lastName": this.lastName,
							"gender": this.gender,
							"dateOfBirth": this.dateOfBirth
						};
						axios
							.post("rest/users/registrationCustomer", user)
							.then(this.$router.push('/'))
					}
					else {
						this.error = true;
						this.errorText = "Lozinke se ne poklapaju."
					}
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
		},
		homePage: function() {
		this.$router.push('/');
		}
	},
	
	mounted() {
		axios
			.get('rest/users')
			.then(response => (this.users = response.data));
	},
});