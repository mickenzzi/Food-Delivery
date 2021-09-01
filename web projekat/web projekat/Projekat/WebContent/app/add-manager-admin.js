Vue.component("adminmanager", {
		data: function() {
		return {
			firstName: "",
			lastName: "",
			dateOfBirth: "",
			gender: "",
			password1: "",
			password2: "",
			employeeType: "",
			username: "",
			error: false,
			errorText: "Sva polja su obavezna",
		
		}
	},
	
	template:
		`
		
	<div class="wrapper5">
    <div class="title">
      Dodaj novog zaposlenog
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
              <option selected value="MALE"> Musko </option>
              <option value="FEMALE"> Zensko </option>
            </select>
          </div>
       </div> 
        <div class="inputfield">
          <label>Datum rodjenja</label>
          <input type="text" class="input" v-model="dateOfBirth" placeholder="Format: yyyy-MM-dd" required>
       </div> 
      <div class="inputfield">
		<input type="button" @click = "goBack" value="Odustani" style ="margin-right: 5px" class="btn">
        <input type="submit" @click = "registrationWorker" value="Dodaj" style ="margin-left: 5px" class="btn">
      </div>
	  <div class="inputfield">
		<p style="color:red;" v-if="error">{{errorText}} </p>
      </div>
    </div>
</div> 
	` ,
	
	methods: {
		
			registrationWorker: function() {
			document.body.style.background = "#009879"
			if(this.firstName !="" && this.lastName!="" && this.gender!="" && this.dateOfBirth!="" && this.username!="" && this.password1!="" && this.password2!="")
			{
				if(this.dateOfBirth.length == 10 && this.dateOfBirth.charAt(4)=='-' && this.dateOfBirth.charAt(7)=='-')
				{
					if(this.password1 == this.password2)
					{
						this.error=false;
						var worker = { 
								  	  "username": this.username, 
									  "password": this.password1, 
									  "firstName": this.firstName, 
									  "lastName": this.lastName,
						              "gender": this.gender,
						              "dateOfBirth":this.dateOfBirth,
									  "userRole": "MANAGER"
					};
					
					
					axios
						.post("rest/users/registrationWorker", worker)
						.then(this.$router.go(-1), sessionStorage.setItem('username',this.username)
							)
					}
					else
					{
						this.error=true;
						this.errorText="Lozinke se ne poklapaju."
					}
				}	
				else
				{
					this.error=true;
					this.errorText="Datum nije unet u ispravnom formatu.";	
				}
			}
			else
			{
				this.error=true;
				this.errorText="Neophodno je uneti sve podatke.";	
			}
		},
		
		goBack: function(){
			document.body.style.background = "white"
			this.$router.go(-1)
		}
		
	},
	
	mounted() {
			document.body.style.background = "#009879";
		
		
	},
	
	
});