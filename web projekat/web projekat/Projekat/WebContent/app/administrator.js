Vue.component("administrator", {
	
	template:
		`
<div>
	<div class="topnav" style= "height: 57px">
  	 <router-link tag="a" active-class="active" to = "/administrator"> Pregled</router-link >
 	 <router-link tag="a" to="/restorani" > Restorani</router-link >
  	 <router-link tag="a" to="/users"> Korisnici</router-link >
	 <router-link tag="a" to="/comments"> Komentari </router-link >
	
	<div style = "float: right;  padding: 14px 16px" >
		<div class="btn-group">
  	<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
    		Podesavanja
 		 </button>
  			<div class="dropdown-menu dropdown-menu-right" style="background: #04AA6D">
    			<router-link  class="dropdown-item" style ="font-size: 15px;
    padding: 5px 2px;" to="/adminprofil">Uredi profil</router-link>
    			<router-link  class="dropdown-item" style ="font-size: 15px;
    padding: 5px 2px;" to="/adminprofil">Odjavi se</router-link>
    			
  			</div>
		</div>
 	</div>	
	</div>
	
	
		
</div>  
	` ,

	methods: {
		myFunction: function() {
	
},

	
	
	},
	mounted() {
		document.body.style.background = "white";
		axios
			.get('rest/users')
			.then(response => (this.users = response.data,
								console.log(this.users)
								
								));
	 window.onclick = function(event) {
 	 if (!event.target.matches('.dropbtn')) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}
	},
});
