Vue.component("addrestaurant", {
		data: function() {
		return {
			restaurantName: "",
			geoLocationWidth: "",
			geoLocationHeight: "",
			restaurantType: "",
			restaurantStatus: "",
			restaurantAdress: "",
		    managers: [],
			selectedManager: "tt",
			imageToUse: "",
			username: "",
			fileToUse: "",
			error: false,
			imageLoadingError: false,
			optionManager : "",
			errorText: "Sva polja su obavezna",
		
		}
	},
	
	template:
		`
	<div class="wrapper5">
    <div class="title">
      Dodaj novi restoran
    </div>
    <div class="form">
       <div class="inputfield">
          <label>Naziv restorana </label>
          <input type="text" class="input" v-model="restaurantName" required>
       </div>  
        <div class="inputfield">
          <label>Tip restorana</label>
         <div class="custom_select">
            <select  v-model="restaurantType" >
              <option selected value="ITALIAN"> Italijanski </option>
              <option value="MEXICAN"> Meksicki </option>
		      <option value="INDIAN"> Indijski </option>
              <option value="CHINESE"> Kineski </option>
			  <option value="FAST_FOOD"> Brza hrana </option>	
              <option value="BARBECUE"> Rostilj  </option>	
            </select>
          </div>
       </div>  
		<div class="inputfield">
          <label>Lokacija(geo-sirina)</label>
          <input type="text" class="input" v-model="geoLocationWidth" required>
       </div> 
	   <div class="inputfield">
          <label>Lokacija(geo-duzina)</label>
          <input type="text" class="input" v-model="geoLocationHeight" required>
       </div> 
	    <div class="inputfield">
          <label>Lokacija(adresa)</label>
          <input type="text" class="input" v-model="restaurantAdress" required>
       </div>   
	    <div class="inputfield">
          <label>Status restorana</label>
         <div class="custom_select">
            <select  v-model="restaurantStatus" >
              <option selected value="OPEN"> Otvoren </option>
              <option value="CLOSED"> Zatvoren </option>
            </select>
          </div>
       </div> 
	   <div class="inputfield">
          <label>Izaberite menadzera</label>
         <div class="custom_select" v-if = "managers.length !== 0" >
            <select  v-model="selectedManager" >
              <option  v-for="m in managers" :value="m.username" > {{m.firstName +" "+ m.lastName +" "+m.username}} </option>            
            </select>
          </div>
           <button class="btn" @click = "addManager" style = "margin-right: 10px" v-if = "managers.length === 0" > 
			dodaj menadzera </button>
       </div>
		<div class="inputfield" style= "display:flex">
          <label style = "flex: 74%">Izaberite logo</label>
           <input style="display:none" type="file" id="file" ref="unos" v-on:change="selectedImage" accept="image/*" requried/>
        	 <button class="btn" style = "margin-right: 10px" v-on:click="$refs.unos.click()"> 
			Izaberite logo </button>
			<label>{{ this.fileToUse.name}}</label>
       </div>   
	   
       
      <div class="inputfield">
		<input type="button"  value="Odustani" style ="margin-right: 5px" class="btn">
        <input type="submit" @click = "registrationRestaurant" value="Dodaj" style ="margin-left: 5px" class="btn">
      </div>
	  <div class="inputfield">
		<p style="color:red;" v-if="error">{{errorText}} </p>
      </div>
    </div>
</div> 
	` ,
	
	
	
	methods: {
			addManager: function(){
				this.$router.push('adminmanager');
			},
		
		
			registrationRestaurant: function() {
				
			   
				if(this.restaurantName !== "" && this.geoLocationWidth !== "" && this.geoLocationHeight !== "" &&
				 this.restaurantType !== "" && this.restaurantStatus !== "" && this.restaurantAdress !== "" ){
					if(this.selectedManager !== null){
						
						if(this.fileToUse !== ""){
							
						    if(!isNaN(this.geoLocationWidth) && !isNaN(this.geoLocationHeight)){
							
							this.error = false;
							var restaurant = {
							"name": this.restaurantName,
							"type": this.restaurantType,
							"status": this.restaurantStatus,
							"longitude": this.geoLocationWidth,
							"latitude":  this.geoLocationHeight,
							"adress": this.restaurantAdress ,
							"logo": "images/" +this.fileToUse.name,
							"managerUsername": this.selectedManager
						}
						
						axios
						.post("rest/restaurants/addRestaurant", restaurant)
						.then(this.$router.push('/restorani'))
						
						
			    							
						}else{
							this.errorText ="Unos dobar format za gepsirini i duzinu";
							this.error = true;
						}
							
													
						}else{
							this.errorText ="Odaberite logo";
							this.error = true;
						}
						
					}else{
						this.errorText ="Odaberite menadzera";
						this.error = true;
					}
					
				}else{
					this.errorText ="Sva polja su obavezna";
					this.error = true;
				}
				
			},
			
			selectedImage(event) {
			event.preventDefault();
			this.fileToUse = event.target.files[0]; 
			this.imageToUse = true;
			
			if(this.fileToUse != null)
			{ 
				this.imageLoadingError = false;
				
				
				const fd = new FormData();
				fd.append('slika', this.fileToUse, this.fileToUse.name)
			
				//axios
					//.post('rest/restorani/dodajSliku')
					//.then(response => {
						//console.log(response);
					//})
			}
			else
			{
				this.greskaUpisSlike = true;
				
			}
      },
	},
	
	mounted() {
			
			
				axios
			.get('rest/users/getAllManagersWithoutRestaurant')
			.then(response => (this.managers = response.data,										
								console.log(this.managers),
								this.selectedManager = sessionStorage.getItem('username'),
								sessionStorage.clear()
								));
								
								
		    document.body.style.background = "#009879";
			
		
		
	},
	
	
});