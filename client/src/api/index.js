import axios from 'axios';

const API = axios.create({ baseURL: 'http://localhost:8888' });

API.interceptors.request.use((req) => {
  if (localStorage.getItem('profile')) {
    req.headers.Authorization = `Bearer ${JSON.parse(localStorage.getItem('profile')).token}`;
  }

  return req;
});

export const fetchPosts = () => API.get('/api/product/all');
export const createPost = (newPost) => API.post('/api/product/addProduct', newPost);
export const updatePost = (id, updatedPost) => API.patch(`/api/product/update/${id}`, updatedPost);
export const deletePost = (id) => API.delete(`/api/product/delete/${id}`);

export const signIn = (formData) => API.post('/api/auth/signin', formData);
export const signUp = (formData) => API.post('/api/auth/signup', formData);
