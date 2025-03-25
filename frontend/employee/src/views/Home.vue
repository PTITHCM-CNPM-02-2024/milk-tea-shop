<template>
  <div class="min-h-screen bg-mint-50 flex">
    <!-- Left Sidebar -->
    <div class="w-64 bg-white shadow-lg p-4 flex flex-col">
      <div class="mb-8">
        <div class="flex items-center text-teal-600 font-bold text-xl">
          <div class="mr-2">🧋</div>
          <div>BubbleLogo</div>
        </div>
      </div>

      <nav class="flex-1">
        <ul class="space-y-2">
          <li v-for="(item, index) in menuItems" :key="index"
              :class="activeMenu === item.id ? 'bg-teal-50 text-teal-600' : 'text-gray-600'"
              class="flex items-center p-3 rounded-lg hover:bg-teal-50 hover:text-teal-600 cursor-pointer transition-colors">
            <div class="w-6 mr-3">
              <component :is="item.icon" class="h-5 w-5" />
            </div>
            <span>{{ item.name }}</span>
          </li>
        </ul>
      </nav>

      <div class="mt-auto">
        <div class="flex items-center p-3 text-gray-600 hover:bg-teal-50 hover:text-teal-600 rounded-lg cursor-pointer">
          <div class="w-6 mr-3">
            <LogOutIcon class="h-5 w-5" />
          </div>
          <span>Logout</span>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="flex-1 flex overflow-hidden">
      <div class="flex-1 overflow-auto p-6">
        <!-- Search Bar -->
        <div class="flex items-center mb-6">
          <div class="relative flex-1 max-w-xl">
            <SearchIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input type="text" placeholder="Search menu..."
                   class="w-full pl-10 pr-4 py-2 rounded-full border border-gray-200 focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent" />
          </div>
          <div class="ml-4 flex items-center">
            <div class="text-sm text-gray-500 mr-2">Cashier: John Doe</div>
            <div class="h-8 w-8 rounded-full bg-teal-500 flex items-center justify-center text-white font-medium">JD</div>
            <BellIcon class="ml-4 h-5 w-5 text-gray-500" />
          </div>
        </div>

        <!-- Categories -->
        <div class="mb-6">
          <h2 class="text-lg font-medium mb-3">Category</h2>
          <div class="grid grid-cols-6 gap-3">
            <div v-for="(category, index) in categories" :key="index"
                 :class="selectedCategory === category.id ? 'bg-teal-100 border-teal-200' : 'bg-white border-gray-100'"
                 class="border rounded-xl p-3 flex flex-col items-center justify-center cursor-pointer hover:shadow-md transition-all"
                 @click="selectedCategory = category.id">
              <div class="w-10 h-10 rounded-lg bg-teal-50 flex items-center justify-center mb-2">
                <component :is="category.icon" class="h-6 w-6 text-teal-500" />
              </div>
              <div class="text-sm font-medium">{{ category.name }}</div>
            </div>
          </div>
        </div>

        <!-- Special Menu -->
        <div>
          <h2 class="text-lg font-medium mb-3">Special Menu for you</h2>
          <div class="grid grid-cols-4 gap-4">
            <div v-for="(product, index) in filteredProducts" :key="index"
                 class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow">
              <div class="h-32 overflow-hidden">
                <img :src="product.image" :alt="product.name" class="w-full h-full object-cover">
              </div>
              <div class="p-3">
                <h3 class="font-medium text-gray-800">{{ product.name }}</h3>
                <div class="flex justify-between items-center mt-2">
                  <button @click="openProductModal(product)"
                          class="bg-teal-500 text-white rounded-full w-8 h-8 flex items-center justify-center hover:bg-teal-600 transition-colors">
                    <PlusIcon class="h-5 w-5" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Order Details Sidebar -->
      <div class="w-80 bg-white shadow-lg overflow-auto">
        <div class="p-4 border-b">
          <h2 class="text-lg font-bold">Order Details</h2>
          <div class="mt-2">
            <div class="font-medium">{{ currentCustomer.name }}</div>
            <div class="text-sm text-gray-500 flex items-center">
              <CalendarIcon class="h-4 w-4 mr-1" />
              <span>{{ formatDate(new Date()) }}</span>
              <ClockIcon class="h-4 w-4 ml-3 mr-1" />
              <span>{{ formatTime(new Date()) }}</span>
            </div>
            <div class="text-sm text-gray-500">{{ currentCustomer.phone }}</div>
          </div>
        </div>

        <div class="flex border-b">
          <div class="flex-1 p-3 text-center font-medium" :class="{'text-teal-600 border-b-2 border-teal-600': orderTab === 'order'}">
            <button @click="orderTab = 'order'" class="w-full">Order <span class="text-xs">({{ orderItems.length }})</span></button>
          </div>
          <div class="flex-1 p-3 text-center font-medium" :class="{'text-teal-600 border-b-2 border-teal-600': orderTab === 'table'}">
            <button @click="orderTab = 'table'" class="w-full">Table <span class="text-xs">({{ currentTable }})</span></button>
          </div>
        </div>

        <div class="p-4">
          <div v-if="orderItems.length === 0" class="text-center py-8 text-gray-400">
            <ShoppingBagIcon class="h-12 w-12 mx-auto mb-2 opacity-30" />
            <p>No items in cart</p>
          </div>

          <div v-else>
            <div v-for="(item, index) in orderItems" :key="index" class="mb-4 pb-4 border-b">
              <div class="flex justify-between mb-1">
                <div class="font-medium">{{ item.name }}</div>
                <div class="font-medium">{{ formatCurrency(item.totalPrice) }}</div>
              </div>
              <div class="flex justify-between text-sm text-gray-500 mb-1">
                <div>Size: {{ item.size }}</div>
                <div>x{{ item.quantity }}</div>
              </div>
              <div v-if="item.toppings && item.toppings.length > 0">
                <div class="text-xs text-gray-500 mb-1">Toppings:</div>
                <div class="flex flex-wrap gap-1">
                  <span v-for="(topping, tIndex) in item.toppings" :key="tIndex"
                        class="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded-full">
                    {{ topping }}
                  </span>
                </div>
              </div>
              <div class="flex justify-between mt-2">
                <button @click="removeItem(index)" class="text-xs text-red-500 hover:text-red-600">Remove</button>
                <div class="flex items-center">
                  <button @click="decreaseQuantity(index)"
                          class="w-6 h-6 rounded-full border border-gray-300 flex items-center justify-center text-gray-500 hover:bg-gray-100">
                    -
                  </button>
                  <span class="mx-2 text-sm">{{ item.quantity }}</span>
                  <button @click="increaseQuantity(index)"
                          class="w-6 h-6 rounded-full border border-gray-300 flex items-center justify-center text-gray-500 hover:bg-gray-100">
                    +
                  </button>
                </div>
              </div>
            </div>

            <!-- Order Summary -->
            <div class="mt-6 space-y-2">
              <div class="flex justify-between text-sm">
                <span>Sub Total</span>
                <span>{{ formatCurrency(subtotal) }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span>Discount</span>
                <span>{{ formatCurrency(discount) }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span>Service Charge</span>
                <span>{{ formatCurrency(serviceCharge) }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span>Tax</span>
                <span>{{ formatCurrency(tax) }}</span>
              </div>
              <div class="flex justify-between font-bold mt-2 pt-2 border-t">
                <span>Total</span>
                <span>{{ formatCurrency(total) }}</span>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="mt-6 grid grid-cols-2 gap-2">
              <button class="py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                Print
              </button>
              <button class="py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors">
                Fire
              </button>
            </div>

            <button class="mt-2 w-full py-2 bg-teal-500 text-white rounded-lg hover:bg-teal-600 transition-colors flex items-center justify-center">
              <CreditCardIcon class="h-5 w-5 mr-2" />
              <span>Charge {{ formatCurrency(total) }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Product Modal -->
    <div v-if="showProductModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl w-full max-w-md p-6">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-bold">{{ selectedProduct.name }}</h3>
          <button @click="showProductModal = false" class="text-gray-500 hover:text-gray-700">
            <XIcon class="h-5 w-5" />
          </button>
        </div>

        <div class="mb-4">
          <img :src="selectedProduct.image" :alt="selectedProduct.name" class="w-full h-48 object-cover rounded-lg">
        </div>

        <!-- Size Selection -->
        <div class="mb-4">
          <h4 class="font-medium mb-2">Size</h4>
          <div class="grid grid-cols-3 gap-2">
            <button v-for="size in sizes" :key="size.id"
                    @click="selectedSize = size.id"
                    :class="selectedSize === size.id ? 'bg-teal-100 border-teal-500 text-teal-700' : 'bg-white border-gray-300 text-gray-700'"
                    class="border rounded-lg py-2 px-3 text-center hover:bg-teal-50 transition-colors">
              <div class="font-medium">{{ size.name }}</div>
              <div class="text-xs">{{ formatCurrency(selectedProduct.price + size.priceAdd) }}</div>
            </button>
          </div>
        </div>

        <!-- Toppings Selection -->
        <div class="mb-6">
          <h4 class="font-medium mb-2">Toppings</h4>
          <div class="grid grid-cols-2 gap-2">
            <div v-for="topping in toppings" :key="topping.id"
                 class="flex items-center space-x-2 border rounded-lg p-2">
              <input type="checkbox"
                     :id="'topping-' + topping.id"
                     v-model="selectedToppings"
                     :value="topping.name"
                     class="rounded text-teal-500 focus:ring-teal-500">
              <label :for="'topping-' + topping.id" class="flex-1 flex justify-between">
                <span>{{ topping.name }}</span>
                <span class="text-sm text-gray-500">+{{ formatCurrency(topping.price) }}</span>
              </label>
            </div>
          </div>
        </div>

        <!-- Add to Order Button -->
        <div class="flex items-center justify-between">
          <div>
            <div class="text-sm text-gray-500">Total Price</div>
            <div class="font-bold text-lg">{{ formatCurrency(calculateItemPrice()) }}</div>
          </div>
          <button @click="addToOrder"
                  class="bg-teal-500 hover:bg-teal-600 text-white px-4 py-2 rounded-lg transition-colors">
            Add to Order
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  LayoutDashboardIcon, ShoppingCartIcon, ClockIcon, ReceiptIcon,
  SettingsIcon, HelpCircleIcon, LogOutIcon, SearchIcon, BellIcon,
  CupSodaIcon, CoffeeIcon, MilkshakeIcon, CakeIcon, FruitIcon, IceCreamIcon,
  PlusIcon, CalendarIcon, ShoppingBagIcon, CreditCardIcon, XIcon
} from 'lucide-vue-next'

// Navigation
const menuItems = [
  { id: 'menu', name: 'Menu', icon: LayoutDashboardIcon },
  { id: 'order-list', name: 'Order List', icon: ShoppingCartIcon },
  { id: 'history', name: 'History', icon: ClockIcon },
  { id: 'bills', name: 'Bills', icon: ReceiptIcon },
  { id: 'settings', name: 'Settings', icon: SettingsIcon },
  { id: 'help', name: 'Help Center', icon: HelpCircleIcon },
]
const activeMenu = ref('menu')

// Categories
const categories = [
  { id: 'milk-tea', name: 'Milk Tea', icon: CupSodaIcon },
  { id: 'fruit-tea', name: 'Fruit Tea', icon: FruitIcon },
  { id: 'coffee', name: 'Coffee', icon: CoffeeIcon },
  { id: 'smoothies', name: 'Smoothies', icon: MilkshakeIcon },
  { id: 'desserts', name: 'Desserts', icon: CakeIcon },
  { id: 'toppings', name: 'Toppings', icon: IceCreamIcon },
]
const selectedCategory = ref('milk-tea')

// Products
const products = [
  {
    id: 1,
    name: 'Classic Milk Tea',
    price: 35000,
    category: 'milk-tea',
    image: '/placeholder.svg?height=200&width=200&text=Classic+Milk+Tea'
  },
  {
    id: 2,
    name: 'Taro Milk Tea',
    price: 40000,
    category: 'milk-tea',
    image: '/placeholder.svg?height=200&width=200&text=Taro+Milk+Tea'
  },
  {
    id: 3,
    name: 'Brown Sugar Milk Tea',
    price: 45000,
    category: 'milk-tea',
    image: '/placeholder.svg?height=200&width=200&text=Brown+Sugar+Milk+Tea'
  },
  {
    id: 4,
    name: 'Matcha Milk Tea',
    price: 42000,
    category: 'milk-tea',
    image: '/placeholder.svg?height=200&width=200&text=Matcha+Milk+Tea'
  },
  {
    id: 5,
    name: 'Lychee Tea',
    price: 38000,
    category: 'fruit-tea',
    image: '/placeholder.svg?height=200&width=200&text=Lychee+Tea'
  },
  {
    id: 6,
    name: 'Peach Tea',
    price: 38000,
    category: 'fruit-tea',
    image: '/placeholder.svg?height=200&width=200&text=Peach+Tea'
  },
  {
    id: 7,
    name: 'Passion Fruit Tea',
    price: 40000,
    category: 'fruit-tea',
    image: '/placeholder.svg?height=200&width=200&text=Passion+Fruit+Tea'
  },
  {
    id: 8,
    name: 'Strawberry Tea',
    price: 42000,
    category: 'fruit-tea',
    image: '/placeholder.svg?height=200&width=200&text=Strawberry+Tea'
  },
]

const filteredProducts = computed(() => {
  return products.filter(product => product.category === selectedCategory.value)
})

// Sizes
const sizes = [
  { id: 'small', name: 'Small', priceAdd: 0 },
  { id: 'medium', name: 'Medium', priceAdd: 5000 },
  { id: 'large', name: 'Large', priceAdd: 10000 },
]

// Toppings
const toppings = [
  { id: 1, name: 'Tapioca Pearls', price: 5000 },
  { id: 2, name: 'Grass Jelly', price: 5000 },
  { id: 3, name: 'Aloe Vera', price: 7000 },
  { id: 4, name: 'Pudding', price: 7000 },
  { id: 5, name: 'Cheese Foam', price: 10000 },
  { id: 6, name: 'Coconut Jelly', price: 7000 },
]

// Order Management
const orderItems = ref([])
const orderTab = ref('order')
const currentTable = ref('A1')
const currentCustomer = ref({
  name: 'Johnson Mitchell',
  phone: '+1(555)123-4567'
})

// Product Modal
const showProductModal = ref(false)
const selectedProduct = ref({})
const selectedSize = ref('medium')
const selectedToppings = ref([])

const openProductModal = (product) => {
  selectedProduct.value = product
  selectedSize.value = 'medium'
  selectedToppings.value = []
  showProductModal.value = true
}

const calculateItemPrice = () => {
  if (!selectedProduct.value || !selectedProduct.value.price) return 0

  const sizeObj = sizes.find(s => s.id === selectedSize.value)
  const basePrice = selectedProduct.value.price + (sizeObj ? sizeObj.priceAdd : 0)

  const toppingsPrice = selectedToppings.value.reduce((total, toppingName) => {
    const topping = toppings.find(t => t.name === toppingName)
    return total + (topping ? topping.price : 0)
  }, 0)

  return basePrice + toppingsPrice
}

const addToOrder = () => {
  const sizeObj = sizes.find(s => s.id === selectedSize.value)
  const itemPrice = calculateItemPrice()

  orderItems.value.push({
    id: Date.now(),
    productId: selectedProduct.value.id,
    name: selectedProduct.value.name,
    size: sizeObj.name,
    basePrice: selectedProduct.value.price,
    sizePrice: sizeObj.priceAdd,
    toppings: [...selectedToppings.value],
    quantity: 1,
    totalPrice: itemPrice
  })

  showProductModal.value = false
}

const increaseQuantity = (index) => {
  orderItems.value[index].quantity++
  updateItemTotalPrice(index)
}

const decreaseQuantity = (index) => {
  if (orderItems.value[index].quantity > 1) {
    orderItems.value[index].quantity--
    updateItemTotalPrice(index)
  }
}

const updateItemTotalPrice = (index) => {
  const item = orderItems.value[index]
  const toppingsPrice = item.toppings.reduce((total, toppingName) => {
    const topping = toppings.find(t => t.name === toppingName)
    return total + (topping ? topping.price : 0)
  }, 0)

  const itemBasePrice = item.basePrice + item.sizePrice + toppingsPrice
  item.totalPrice = itemBasePrice * item.quantity
}

const removeItem = (index) => {
  orderItems.value.splice(index, 1)
}

// Order calculations
const subtotal = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.totalPrice, 0)
})

const discount = computed(() => {
  return 0 // You can implement discount logic here
})

const serviceCharge = computed(() => {
  return 0 // You can implement service charge logic here
})

const tax = computed(() => {
  return subtotal.value * 0.1
})

const total = computed(() => {
  return subtotal.value - discount.value + serviceCharge.value + tax.value
})

// Utility functions
const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

const formatDate = (date) => {
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(date)
}

const formatTime = (date) => {
  return new Intl.DateTimeFormat('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  }).format(date)
}
</script>

<style>
.bg-mint-50 {
  background-color: #f0faf5;
}
</style>