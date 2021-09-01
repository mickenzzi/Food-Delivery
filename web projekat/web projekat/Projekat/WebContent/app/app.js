
const Home = { template: '<home></home>' }
const Registration = { template: '<registration></registration>' }
const Customer = { template: '<homeCustomer></homeCustomer>' }
const Administrator = {template: '<administrator></administrator>'}
const Restorani = {template :'<restorani></restorani>'}
const Comments = {template : '<comments></comments>'}
const Users = {template : '<users></users>'}
const NewEmployee = {template: '<newemployee></newemployee>'}
const CustomerData = {template: '<customerData></customerData>'}
const NewRestaurant = {template: '<addrestaurant></addrestaurant>'}
const AdminProfil = {template: '<adminprofil></adminprofil>'}
const AdminManager = {template: '<adminmanager></adminmanager>'}
const RestaurantHome = {template: '<restaurantHome></restaurantHome>'}
const Basket = {template: '<basket></basket>'}
const OrderHistoryCustomer = {template: '<orderHistoryCustomer></orderHistoryCustomer>'}
const CustomerComment = {template: '<customerComment></customerComment>'}
const GuestHome = {template: '<guestHome></guestHome>'}
const GuestRestaurantHome = {template: '<guestRestaurantHome></guestRestaurantHome>'}

const router = new VueRouter({
	mode: 'hash',
	routes: [
		{ path: '/', component: Home },
		{ path: '/registration', component: Registration },
		{ path: '/homeCustomer', component: Customer},
		{ path: '/administrator', component: Administrator},
		{ path: '/restorani', component: Restorani},
		{ path: '/comments', component: Comments},
		{ path: '/users', component: Users},
		{ path: '/newemployee', component: NewEmployee},
		{ path: '/customerData', component: CustomerData},
		{ path: '/addrestaurant', component: NewRestaurant},
		{ path: '/adminprofil', component: AdminProfil},
		{ path: '/adminmanager', component: AdminManager},
		{ path: '/restaurantHome/:id', component: RestaurantHome},
		{ path: '/basket', component: Basket},
		{ path: '/orderHistoryCustomer', component: OrderHistoryCustomer},
		{ path: '/customerComment/:id', component: CustomerComment},
		{ path: '/guestHome', component: GuestHome},
		{ path: '/guestRestaurantHome/:id', component: GuestRestaurantHome}
	]
});

var app = new Vue({
	router,
	el: '#application'
});