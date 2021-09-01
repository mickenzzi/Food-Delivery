Vue.component("users", {
		data: function() {
		return {
			users: null,
			filterUsers: null,
			role: "ALL",
			status: "ALL",
			filterFirstName: "",
			filterLastName: "",
			filterUsername: "",
		
		}
	},
	
	template:
		`
	<div>
	<div class="topnav" style= "height: 57px">
  	 <router-link tag="a"  to = "/administrator"> Pregled</router-link >
 	 <router-link tag="a"  to="/restorani" > Restorani</router-link >
  	 <router-link tag="a" active-class="active" to="/users"> Korisnici</router-link >
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
    padding: 5px 2px;" to="/admin-profile">Odjavi se</router-link>
    			
  			</div>
		</div>
 	</div>	
	</div>
	
	<div  style = " margin-left: auto;  margin-right: auto;  width: 800px;  margin-top: 20px;">
	<h2 style ="text-align:center"> Registrovani korisnici </h2><br>
	
	<div class="row" style="margin: 0 auto; display:block; width: 100%;">
	 	<div class="col-md-2" style= "margin-left: 10px"> <label style = "height: 30px; width: 150 px" > Pretraga  </label> </div>
    	<div class="col-md-2" style= "padding: 0px"> <input v-model ="filterFirstName" style="width: 150px; height: 30px"   type="search"  placeholder='Ime' > </div>
    	<div class="col-md-2"  style= "padding: 0px"> <input v-model ="filterLastName" style="width: 150px;  height: 30px"   type="search"  placeholder='Prezime' ></div>
    	<div class="col-md-2"  style= "padding: 0px"> <input v-model ="filterUsername" style="width: 150px;  height: 30px"   type="search"  placeholder='Korisnicko ime' ></div>
		<div class="col-md-2"  style= "margin-left: 40px"> <button @click = "searchUsers" style = "height:30px" type="submit" class="searchButton"> Pretrazi </button></div>	
    </div>
	<br> </br>
	
	<div class="row" style="margin: 0 auto; display:block; width: 100%;">
		<div class="col-md-2" style= "margin-left: 10px"> <label style = "height: 30px; width: 150 px" > Filter  </label> </div>
    		<div class="col-md-2" style= "padding: 0px"> <select v-on:change="filterRole" v-model="role"  style = "width: 120px; height: 30px" >
    			<option value="ALL">SVI</option>
    			<option value="ADMINISTRATOR">Administrator</option>
    			<option value="MANAGER">Menadzer</option>
				<option value="CUSTOMER">Kupac</option>
				<option value="DELIVERER">Dostavljac</option>
 				 </select> 
 			</div>
		
    	<div class="col-md-2"  style= "padding: 0px"> 	<select v-on:change="filterStatus" v-model="status" style = "width: 150px; height: 30px">
    		<option value="ALL">SVI</option>
    		<option value="GOLD"> Zlatni </option>
    		<option value="SILVER"> Srebrni </option>
			<option value="BRONZE"> Bronzani </option>
  			</select> 
		</div>	
	</div>
	
	</div>
	
	<br> </br>
	
<table  style ="margin-top: 10px" class="content-table table-sortable" >
  <thead>
    <tr>
      <th>Ime</th>
      <th>Prezime</th>
      <th>Korisnicko ime</th>
	  <th> Uloga </th>
	  <th> Tip </th>
	  <th>Broj bodova</th>
	  <th>Obrisi</th>
	  <th>Blokiraj/Odblokiraj</th>	 
    </tr>
  </thead>
  <tbody>
   <tr v-for="i in users" >
			<td > {{i.firstName}}</td>
			<td > {{i.lastName}}</td>
			<td > {{i.username}}</td>
			<td > {{i.role}} </td>
			<td > {{i.customerType}} </td>
			<td > {{i.collectedPoint}} </td>
			<td class="td"><button  v-if=" !(i.role === 'ADMINISTRATOR')"  type="button"
			@click = "deleteUser(i)" style = " border-color: #f44336;
  color: red;   border: 2px solid;"  class="btn btn-outline-danger"> Obrisi </button></td>		
			<td  v-if=" !(i.role === 'ADMINISTRATOR') && i.blocked == false"><button type="button"
			@click = "blockUser(i)" style = " border-color: #04AA6D;
  color: green;   border: 2px solid;" class="btn btn-outline-success">
			Blokiraj </button></td>			
			<td v-if=" !(i.role === 'ADMINISTRATOR') && i.blocked == true " ><button type="button"
			@click = "deblockUser(i)" style = " border-color: #f44336;
  color: red;   border: 2px solid;"  class="btn btn-outline-danger"> Odblokiraj </button></td>
			
		</tr>
  </tbody>
</table>
	<div   style=" margin-left: auto;  margin-right: auto;  width: 1000px;  margin-top: 20px;">        
                <button class= "button5" @click = "goToUserForm" > Dodaj novog korisnika </button>
      </div>
	</div>  
	` ,
	
	methods: {
		searchUsers: function(){
			
			this.role = "ALL";
			this.status = "ALL",
			axios.get('rest/users/getFilterUsers', {
    params: {
      firstName: this.filterFirstName,
      lastName: this.filterLastName,
	  username: this.filterUsername
    },
  })
			.then(response => (this.users = response.data,			
								this.filterUsers = response.data,
								console.log(this.users)));
			
		},
		
		
		goToUserForm: function(){
			this.$router.push('/newemployee');	
		},
		
		blockUser: function(k){
			var userToBlock = {"customerUsername" : k.username }
			k.blocked = true;
			axios
	          .post('rest/users/blockUser', userToBlock);
	          
		},
		
		deblockUser: function(k){
			k.blocked = false;
			var userToDeblock = {"customerUsername" : k.username }
			axios
	          .post('rest/users/unblockUser', userToDeblock);
			console.log(this.users);
	           
		},
		
		filterDelete: function(){			
			this.users = this.users.filter(function (e){ return e.deleted === false;});
			this.filterUsers = this.users;
		},
	
  		deleteUser: function(k) {
			k.deleted = true;
			axios
	          .delete('rest/users/deleteUser'+ k.username)
	        console.log(this.users);
			this.filterDelete();
	       
  		},

		
		
		
		filterRole: function(){     
		
			
            if (this.role === 'ALL' && this.status === 'ALL') {
	
                this.users = this.filterUsers;
            } else if (this.role !== 'ALL' && this.status === 'ALL')  { 
				var role = this.role;
                this.users = this.filterUsers.filter(function (e){ console.log(e.role + " " + role);
	return e.role === role;})
            } else if (this.role === 'ALL' && this.status !== 'ALL')  { 
	            var status = this.status;
                this.users = this.filterUsers.filter(function (e){ return e.customerType === status;})
            } else if (this.role !== 'ALL' && this.status !== 'ALL')  { 
				var role = this.role;
				var status = this.status;
                this.users = this.filterUsers.filter(function (e){ return e.customerType == status &&  e.role == role;})
            }
        },    
		
		filterStatus: function(){
				
            if (this.role === 'ALL' && this.status === 'ALL') {	
                this.users = this.filterUsers;
            } else if (this.role !== 'ALL' && this.status === 'ALL')  { 
				var role = this.role;
                this.users = this.filterUsers.filter(function (e){ console.log(e.role + " " + role);
	return e.role === role;})
            } else if (this.role === 'ALL' && this.status !== 'ALL')  { 
	            var status = this.status;
                this.users = this.filterUsers.filter(function (e){ return e.customerType === status;})
            } else if (this.role !== 'ALL' && this.status !== 'ALL')  { 
				var role = this.role;
				var status = this.status;
                this.users = this.filterUsers.filter(function (e){ return e.customerType == status &&  e.role == role;})
            }
        }    
    
	},
	mounted() {
			document.body.style.background = "white";
		console.log("ualoo");		    
		function sortTableByColumn(table, column, asc = true) {
    const dirModifier = asc ? 1 : -1;
    const tBody = table.tBodies[0];
    const rows = Array.from(tBody.querySelectorAll("tr"));

    const sortedRows = rows.sort((a, b) => {
        const aColText = a.querySelector(`td:nth-child(${ column + 1 })`).textContent.trim();
        const bColText = b.querySelector(`td:nth-child(${ column + 1 })`).textContent.trim();

        return aColText > bColText ? (1 * dirModifier) : (-1 * dirModifier);
    });

   
    while (tBody.firstChild) {
        tBody.removeChild(tBody.firstChild);
    }

    tBody.append(...sortedRows);

    // Remember how the column is currently sorted
    table.querySelectorAll("th").forEach(th => th.classList.remove("th-sort-asc", "th-sort-desc"));
    table.querySelector(`th:nth-child(${ column + 1})`).classList.toggle("th-sort-asc", asc);
    table.querySelector(`th:nth-child(${ column + 1})`).classList.toggle("th-sort-desc", !asc);
}

		
		
		document.querySelectorAll(".table-sortable th").forEach(headerCell => {
    headerCell.addEventListener("click", () => {
        const tableElement = headerCell.parentElement.parentElement.parentElement;
        const headerIndex = Array.prototype.indexOf.call(headerCell.parentElement.children, headerCell);
		if(!(headerIndex === 4 || headerIndex === 3 )){
		  const currentIsAscending = headerCell.classList.contains("th-sort-asc");
		  sortTableByColumn(tableElement, headerIndex, !currentIsAscending);	
		}
    });
});		

		
		axios
			.get('rest/users')
			.then(response => (this.users = response.data,			
								this.filterUsers = response.data,
								console.log(this.users)));
	},
	
	
});