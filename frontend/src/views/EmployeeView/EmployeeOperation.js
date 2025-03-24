document.querySelector('.category_product').addEventListener('wheel', function(event) {
    event.preventDefault(); // Chặn cuộn dọc
    this.scrollLeft += event.deltaY; // Chuyển hướng cuộn
});
