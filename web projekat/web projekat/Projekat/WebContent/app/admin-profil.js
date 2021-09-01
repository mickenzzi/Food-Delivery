Vue.component("adminprofil", {
	data: function() {
		return {
			
			usernameOld: "",
			firstNameEdit: "",
			lastNameEdit: "",
			dateOfBirthEdit: "",
			genderEdit: "",
			passwordEdit: "",
			usernameEdit: "",
			admin: null,
			password1: "",
			password2:  "",
			password3: "",
			errorText: "Sva polja su obavezna",
			changePasswordFlag: false,
			passwordChange: "Zameni lozinku",
			users: null,
			error: false
			}
	},
	
	template:
		`
<div class="wrapper5">
    <div class="title">
      Uredi profil administrator
    </div>
    <div class="form">
       <div class="inputfield">
          <label>Ime </label>
          <input type="text" class="input" v-model="firstNameEdit" required>
       </div>  
        <div class="inputfield">
          <label>Prezime</label>
          <input type="text" class="input" v-model="lastNameEdit"  required>
       </div>  
		<div class="inputfield">
          <label>Korisnicko ime</label>
          <input type="text" class="input" v-model="usernameEdit" required>
       </div> 
       <div class="inputfield">
          <label>Lozinka</label>
          <input type="button" @click = "changePassword" v-model = "passwordChange" style ="margin-right: 5px" class="btn">
       </div>  
	 
	  <div class="inputfield" v-if = "changePasswordFlag">
          <label>Unesi staru lozniku lozinku</label>
          <input type="password" class="input" name="current-password" autocomplete="off" v-model="password1" required>
       </div> 
      <div class="inputfield" v-if = "changePasswordFlag">
          <label>Unesi novu lozniku</label>
          <input type="password" class="input" name = "new-password" autocomplete="off"  v-model="password2" required>
       </div>
	    <div class="inputfield" v-if = "changePasswordFlag">
          <label>Potvrdi lozniku</label>
          <input type="password" class="input"  v-model="password3" autocomplete="off" required>
       </div> 
	  
        <div class="inputfield" style>
          <label>Pol</label>
          <div class="custom_select">
            <select  v-model="genderEdit" >
              <option value="MUSKI"> Musko </option>
              <option value="ZENSKI"> Zensko </option>
            </select>
          </div>
       </div> 
        <div class="inputfield">
          <label>Datum rodjenja</label>
          <input type="text" class="input" v-model="dateOfBirthEdit" placeholder="Format: yyyy-MM-dd" required>
       </div> 
      <div class="inputfield">
		<input type="button" @click = "goBack" value="Odustani" style ="margin-right: 5px" class="btn">
		<input type="button" @click = "editUser" value="Sacuvaj" style ="margin-left: 5px" class="btn">
      </div>
	  <div class="inputfield">
		<p style="color:red;" v-if="error">{{errorText}} </p>
      </div>
    </div>
</div> 
	` ,

	methods: {
		goBack: function(){
			document.body.style.background = "white"
			this.$router.go(-1);
		},
		
		changePassword: function() {
			if(this.passwordChange === "Zameni lozinku"){
				this.passwordChange = "Odustani od provjere";
				this.changePasswordFlag = true;
			}else{
				this.passwordChange = "Zameni lozinku"
				this.changePasswordFlag = false;
			}
		},
		
		editUser: function(){

			if(this.firstNameEdit !="" && this.lastNameEdit !="" && this.genderEdit !="" && this.dateOfBirthEdit!="" 
			&& this.usernameEdit!= "")
			{
				if(this.dateOfBirthEdit.length == 10 && this.dateOfBirthEdit.charAt(4)=='-' && this.dateOfBirthEdit.charAt(7)=='-')
				{
					if(this.changePasswordFlag == true)
					{
						//ako se lozinka mijenjaa
						if(this.password1 === this.passwordEdit && this.password2 === this.password3){
							var editUser = {
									  "oldUsername": this.usernameOld,
									  "username": this.usernameEdit, 
									  "password": this.password2, 
									  "firstName": this.firstNameEdit, 
									  "lastName": this.lastNameEdit,
						              "gender": this.genderEdit,
						              "dateOfBirth":this.dateOfBirthEdit
										    }
							axios
							.put("rest/users/editLoggedUser", editUser)
						
							
						}else{
							this.error=true;
							this.errorText="Greska prilikom izmene lozinke.";	
						}
						
						
					}
					else
					{
						//ako se lozinka ne mijenjaa
						var editUser = {
									  "oldUsername": this.usernameOld,
									  "username": this.usernameEdit, 
									  "password": this.passwordEdit, 
									  "firstName": this.firstNameEdit, 
									  "lastName": this.lastNameEdit,
						              "gender": this.genderEdit,
						              "dateOfBirth":this.dateOfBirthEdit
										}
						console.log(editUser)
							axios
							.put("rest/users/editLoggedUser", editUser)				
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
			
		}
	
	},
	mounted() {
		document.body.style.background = "#009879";
		
		axios
			.get('rest/users/getLoginUser')
			.then(response => (this.admin = response.data,
							   this.usernameOld = this.admin.username,
							   this.usernameEdit = this.admin.username,	
							   this.firstNameEdit = this.admin.firstName,
							   this.lastNameEdit = this.admin.lastName,
							   this.passwordEdit = this.admin.password,
							   this.dateOfBirthEdit = this.admin.dateOfBirth,
							   this.genderEdit = this.admin.gender					
								));
								
		axios
		.get('rest/users')
		.then(response => (this.users = response.data));
	},
});